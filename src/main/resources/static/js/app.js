// State Management
const STATE = {
    token: localStorage.getItem('hms_token') || null,
    user: JSON.parse(localStorage.getItem('hms_user') || 'null'),
    patients: [],
    doctors: [],
    medicines: []
};

// Initialize Application
document.addEventListener('DOMContentLoaded', () => {
    updateDateDisplay();
    if (STATE.token && STATE.user) {
        showApp();
    } else {
        showAuth();
    }
});

function updateDateDisplay() {
    const el = document.getElementById('currentDate');
    if (el) {
        const options = { weekday: 'long', year: 'numeric', month: 'short', day: 'numeric' };
        el.innerText = new Date().toLocaleDateString('en-US', options);
    }
}

// ========================================================
// AUTHENTICATION
// ========================================================
function switchAuthTab(tab) {
    const tabs = document.querySelectorAll('.auth-tab');
    tabs.forEach(t => t.classList.remove('active'));
    if (tab === 'login') {
        tabs[0].classList.add('active');
        document.getElementById('loginForm').classList.remove('hidden');
        document.getElementById('registerForm').classList.add('hidden');
    } else {
        tabs[1].classList.add('active');
        document.getElementById('loginForm').classList.add('hidden');
        document.getElementById('registerForm').classList.remove('hidden');
    }
}

async function handleLogin(e) {
    e.preventDefault();
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value;
    await loginUser(username, password);
}

function fillAndLogin(username, password) {
    document.getElementById('loginUsername').value = username;
    document.getElementById('loginPassword').value = password;
    loginUser(username, password);
}

async function loginUser(username, password) {
    clearAlert('authAlert');
    try {
        const res = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        const data = await res.json();

        if (res.ok && data.accessToken) {
            STATE.token = data.accessToken;
            STATE.user = {
                username: data.username,
                roles: data.roles || []
            };
            localStorage.setItem('hms_token', STATE.token);
            localStorage.setItem('hms_user', JSON.stringify(STATE.user));
            showApp();
            showToast('Signed in successfully as ' + username);
        } else {
            showAlert('authAlert', data.message || 'Invalid username or password', 'danger');
        }
    } catch (err) {
        showAlert('authAlert', 'Connection error. Ensure backend server is running on port 8080.', 'danger');
    }
}

async function handleRegister(e) {
    e.preventDefault();
    clearAlert('authAlert');

    const fullName = document.getElementById('regFullName').value.trim();
    const username = document.getElementById('regUsername').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const password = document.getElementById('regPassword').value;
    const role = document.getElementById('regRole').value;

    try {
        const res = await fetch('/api/auth/signup', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ fullName, username, email, password, roles: [role] })
        });
        const data = await res.json();

        if (res.ok) {
            showAlert('authAlert', 'Account registered! You can now sign in.', 'success');
            switchAuthTab('login');
        } else {
            showAlert('authAlert', data.message || 'Registration failed', 'danger');
        }
    } catch (err) {
        showAlert('authAlert', 'Network error during registration.', 'danger');
    }
}

function handleLogout() {
    STATE.token = null;
    STATE.user = null;
    localStorage.removeItem('hms_token');
    localStorage.removeItem('hms_user');
    showAuth();
}

function showAuth() {
    document.getElementById('authView').classList.remove('hidden');
    document.getElementById('mainView').classList.add('hidden');
}

function showApp() {
    document.getElementById('authView').classList.add('hidden');
    document.getElementById('mainView').classList.remove('hidden');

    document.getElementById('currentUserName').innerText = STATE.user.username;
    document.getElementById('currentUserRole').innerText = STATE.user.roles.join(', ') || 'USER';

    loadAllDashboardData();
}

// ========================================================
// API HELPER
// ========================================================
async function apiFetch(endpoint, options = {}) {
    options.headers = options.headers || {};
    if (STATE.token) {
        options.headers['Authorization'] = 'Bearer ' + STATE.token;
    }
    options.headers['Content-Type'] = 'application/json';

    const res = await fetch(endpoint, options);
    if (res.status === 401 || res.status === 403) {
        if (res.status === 401) {
            handleLogout();
            throw new Error('Session expired');
        }
    }
    return res;
}

// ========================================================
// NAVIGATION
// ========================================================
function navigate(sectionId) {
    document.querySelectorAll('.nav-item').forEach(item => {
        if (!item.classList.contains('external-link')) item.classList.remove('active');
    });
    const link = document.querySelector(`a[href="#${sectionId}"]`);
    if (link) link.classList.add('active');

    document.querySelectorAll('.content-section').forEach(sec => sec.classList.remove('active'));
    const target = document.getElementById(`section-${sectionId}`);
    if (target) target.classList.add('active');

    const titles = {
        dashboard: 'Dashboard Overview',
        patients: 'Patient Directory',
        doctors: 'Specialists & Medical Staff',
        appointments: 'Appointments Schedule',
        prescriptions: 'Clinical Prescriptions',
        pharmacy: 'Pharmacy & Stock Inventory',
        billing: 'Patient Invoices & Billing'
    };
    document.getElementById('sectionTitle').innerText = titles[sectionId] || 'Hospital Management';

    if (sectionId === 'patients') loadPatients();
    if (sectionId === 'doctors') loadDoctors();
    if (sectionId === 'appointments') loadAppointments();
    if (sectionId === 'prescriptions') loadPrescriptions();
    if (sectionId === 'pharmacy') loadPharmacy();
    if (sectionId === 'billing') loadBilling();
}

// ========================================================
// DATA LOADERS
// ========================================================
async function loadAllDashboardData() {
    await Promise.allSettled([
        loadDoctors(),
        loadPatients(),
        loadAppointments(),
        loadPharmacy()
    ]);
}

async function loadPatients() {
    try {
        const res = await apiFetch('/api/patients');
        if (res.ok) {
            const data = await res.json();
            STATE.patients = data;
            document.getElementById('statPatientsCount').innerText = data.length;

            const tbody = document.getElementById('patientsTbody');
            if (!tbody) return;
            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" class="text-center">No patients registered yet.</td></tr>';
                return;
            }
            tbody.innerHTML = data.map(p => `
                <tr>
                    <td><strong>#${p.id}</strong></td>
                    <td>${p.firstName} ${p.lastName}</td>
                    <td>${calculateAge(p.dateOfBirth)} yrs / ${p.gender}</td>
                    <td><span class="badge badge-info">${p.bloodGroup || 'N/A'}</span></td>
                    <td>${p.phone}</td>
                    <td>${p.emergencyContactName || '-'} (${p.emergencyContactPhone || '-'})</td>
                    <td>${p.registrationDate || '-'}</td>
                </tr>
            `).join('');
            populatePatientDropdowns(data);
        }
    } catch (e) {
        console.error('Error loading patients', e);
    }
}

async function loadDoctors() {
    try {
        const res = await apiFetch('/api/doctors');
        if (res.ok) {
            const data = await res.json();
            STATE.doctors = data;
            document.getElementById('statDoctorsCount').innerText = data.length;

            const tbody = document.getElementById('doctorsTbody');
            if (!tbody) return;
            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center">No doctors registered.</td></tr>';
                return;
            }
            tbody.innerHTML = data.map(d => `
                <tr>
                    <td><strong>Dr. ${d.firstName} ${d.lastName}</strong></td>
                    <td><span class="badge badge-info">${d.specialization}</span></td>
                    <td>${d.qualification}</td>
                    <td>$${d.consultationFee ? parseFloat(d.consultationFee).toFixed(2) : '0.00'}</td>
                    <td>${d.phone}</td>
                    <td><span class="badge ${d.available ? 'badge-success' : 'badge-secondary'}">${d.available ? 'AVAILABLE' : 'OFF DUTY'}</span></td>
                </tr>
            `).join('');
            populateDoctorDropdowns(data);
        }
    } catch (e) {
        console.error('Error loading doctors', e);
    }
}

async function loadAppointments() {
    try {
        const res = await apiFetch('/api/appointments');
        if (res.ok) {
            const data = await res.json();
            document.getElementById('statAppointmentsCount').innerText = data.length;

            // Dashboard preview table
            const dashTbody = document.getElementById('dashboardAppointmentsTbody');
            if (dashTbody) {
                dashTbody.innerHTML = data.slice(0, 5).map(a => `
                    <tr>
                        <td>${a.patientName || 'Patient #' + a.patientId}</td>
                        <td>${a.doctorName || 'Dr. #' + a.doctorId}</td>
                        <td>${a.appointmentDate} at ${a.appointmentTime}</td>
                        <td><span class="badge ${getStatusBadgeClass(a.status)}">${a.status}</span></td>
                    </tr>
                `).join('') || '<tr><td colspan="4" class="text-center">No upcoming appointments</td></tr>';
            }

            // Full appointments table
            const tbody = document.getElementById('appointmentsTbody');
            if (!tbody) return;
            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" class="text-center">No appointments found.</td></tr>';
                return;
            }
            tbody.innerHTML = data.map(a => `
                <tr>
                    <td><strong>#${a.id}</strong></td>
                    <td>${a.patientName || 'Patient #' + a.patientId}</td>
                    <td>${a.doctorName || 'Dr. #' + a.doctorId}</td>
                    <td>${a.appointmentDate}</td>
                    <td>${a.appointmentTime}</td>
                    <td>${a.reason || '-'}</td>
                    <td><span class="badge ${getStatusBadgeClass(a.status)}">${a.status}</span></td>
                    <td>
                        ${a.status === 'SCHEDULED' ? `
                            <button class="btn btn-sm btn-outline" onclick="completeAppointment(${a.id})">Complete</button>
                        ` : '-'}
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error('Error loading appointments', e);
    }
}

async function loadPharmacy() {
    try {
        const res = await apiFetch('/api/pharmacy/medicines');
        if (res.ok) {
            const data = await res.json();
            STATE.medicines = data;
            document.getElementById('statMedicinesCount').innerText = data.length;

            // Dashboard low stock table
            const stockTbody = document.getElementById('dashboardStockTbody');
            if (stockTbody) {
                stockTbody.innerHTML = data.slice(0, 5).map(m => {
                    const isLow = m.stockQuantity <= (m.reorderLevel || 20);
                    return `
                        <tr>
                            <td><strong>${m.name}</strong></td>
                            <td>${m.stockQuantity} units</td>
                            <td>${m.reorderLevel} units</td>
                            <td><span class="badge ${isLow ? 'badge-danger' : 'badge-success'}">${isLow ? 'LOW STOCK' : 'IN STOCK'}</span></td>
                        </tr>
                    `;
                }).join('') || '<tr><td colspan="4" class="text-center">No medicines in inventory</td></tr>';
            }

            // Full pharmacy table
            const tbody = document.getElementById('pharmacyTbody');
            if (!tbody) return;
            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" class="text-center">No medicines in inventory.</td></tr>';
                return;
            }
            tbody.innerHTML = data.map(m => {
                const isLow = m.stockQuantity <= (m.reorderLevel || 20);
                return `
                    <tr>
                        <td>#${m.id}</td>
                        <td><strong>${m.name}</strong></td>
                        <td>${m.manufacturer}</td>
                        <td><code>${m.batchNumber}</code></td>
                        <td>$${m.price ? parseFloat(m.price).toFixed(2) : '0.00'}</td>
                        <td>${m.stockQuantity}</td>
                        <td>${m.expiryDate}</td>
                        <td><span class="badge ${isLow ? 'badge-danger' : 'badge-success'}">${isLow ? 'REORDER' : 'OPTIMAL'}</span></td>
                    </tr>
                `;
            }).join('');
            populateMedicineDropdowns(data);
        }
    } catch (e) {
        console.error('Error loading pharmacy', e);
    }
}

async function loadPrescriptions() {
    try {
        const res = await apiFetch('/api/prescriptions');
        if (res.ok) {
            const data = await res.json();
            const tbody = document.getElementById('prescriptionsTbody');
            if (!tbody) return;
            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center">No prescriptions issued yet.</td></tr>';
                return;
            }
            tbody.innerHTML = data.map(r => `
                <tr>
                    <td><strong>#${r.id}</strong></td>
                    <td>${r.patientName || 'Patient #' + r.patientId}</td>
                    <td>Dr. ${r.doctorName || r.doctorId}</td>
                    <td>${r.prescriptionDate || '-'}</td>
                    <td>${r.diagnosis || '-'}</td>
                    <td>
                        ${(r.items || []).map(i => `<div>• <strong>${i.medicineName || 'Medicine'}</strong>: ${i.dosage} (${i.frequency}) for ${i.durationDays}d</div>`).join('') || '-'}
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error('Error loading prescriptions', e);
    }
}

async function loadBilling() {
    try {
        const res = await apiFetch('/api/billing/bills');
        if (res.ok) {
            const data = await res.json();
            const tbody = document.getElementById('billingTbody');
            if (!tbody) return;
            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" class="text-center">No invoices generated.</td></tr>';
                return;
            }
            tbody.innerHTML = data.map(b => `
                <tr>
                    <td><strong>${b.invoiceNumber || 'INV-' + b.id}</strong></td>
                    <td>${b.patientName || 'Patient #' + b.patientId}</td>
                    <td>${b.billDate || '-'}</td>
                    <td>$${b.totalAmount ? parseFloat(b.totalAmount).toFixed(2) : '0.00'}</td>
                    <td>$${b.paidAmount ? parseFloat(b.paidAmount).toFixed(2) : '0.00'}</td>
                    <td>${b.paymentMethod || 'PENDING'}</td>
                    <td><span class="badge ${b.status === 'PAID' ? 'badge-success' : 'badge-warning'}">${b.status}</span></td>
                    <td>
                        ${b.status !== 'PAID' ? `
                            <button class="btn btn-sm btn-primary" onclick="markBillPaid(${b.id})">Pay Bill</button>
                        ` : '<span class="badge badge-success">Settled</span>'}
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error('Error loading billing', e);
    }
}

// ========================================================
// MODAL SUBMISSIONS
// ========================================================
async function submitPatient(e) {
    e.preventDefault();
    const payload = {
        firstName: document.getElementById('pFirstName').value.trim(),
        lastName: document.getElementById('pLastName').value.trim(),
        dateOfBirth: document.getElementById('pDob').value,
        gender: document.getElementById('pGender').value,
        bloodGroup: document.getElementById('pBloodGroup').value,
        phone: document.getElementById('pPhone').value.trim(),
        address: document.getElementById('pAddress').value.trim(),
        emergencyContactName: document.getElementById('pEmergencyName').value.trim(),
        emergencyContactPhone: document.getElementById('pEmergencyPhone').value.trim()
    };

    const res = await apiFetch('/api/patients', {
        method: 'POST',
        body: JSON.stringify(payload)
    });

    if (res.ok) {
        closeModal('patientModal');
        showToast('Patient successfully registered');
        loadPatients();
    } else {
        alert('Failed to register patient');
    }
}

async function submitAppointment(e) {
    e.preventDefault();
    const payload = {
        patientId: document.getElementById('aptPatientId').value,
        doctorId: document.getElementById('aptDoctorId').value,
        appointmentDate: document.getElementById('aptDate').value,
        appointmentTime: document.getElementById('aptTime').value + ':00',
        reason: document.getElementById('aptReason').value.trim()
    };

    const res = await apiFetch('/api/appointments', {
        method: 'POST',
        body: JSON.stringify(payload)
    });

    if (res.ok) {
        closeModal('appointmentModal');
        showToast('Appointment successfully scheduled');
        loadAppointments();
    } else {
        alert('Failed to book appointment');
    }
}

async function submitMedicine(e) {
    e.preventDefault();
    const payload = {
        name: document.getElementById('mName').value.trim(),
        manufacturer: document.getElementById('mManufacturer').value.trim(),
        batchNumber: document.getElementById('mBatch').value.trim(),
        price: parseFloat(document.getElementById('mPrice').value),
        stockQuantity: parseInt(document.getElementById('mStock').value),
        reorderLevel: parseInt(document.getElementById('mReorder').value),
        expiryDate: document.getElementById('mExpiry').value
    };

    const res = await apiFetch('/api/pharmacy/medicines', {
        method: 'POST',
        body: JSON.stringify(payload)
    });

    if (res.ok) {
        closeModal('medicineModal');
        showToast('Medicine added to pharmacy inventory');
        loadPharmacy();
    } else {
        alert('Failed to add medicine');
    }
}

async function submitPrescription(e) {
    e.preventDefault();
    const medId = document.querySelector('.rx-med-id').value;
    const dosage = document.querySelector('.rx-dosage').value;
    const frequency = document.querySelector('.rx-freq').value;
    const durationDays = parseInt(document.querySelector('.rx-duration').value);

    const payload = {
        patientId: document.getElementById('rxPatientId').value,
        doctorId: document.getElementById('rxDoctorId').value,
        diagnosis: document.getElementById('rxDiagnosis').value.trim(),
        items: [{ medicineId: medId, dosage, frequency, durationDays }]
    };

    const res = await apiFetch('/api/prescriptions', {
        method: 'POST',
        body: JSON.stringify(payload)
    });

    if (res.ok) {
        closeModal('prescriptionModal');
        showToast('Prescription recorded and pharmacy order dispatched');
        loadPrescriptions();
    } else {
        alert('Failed to issue prescription');
    }
}

async function completeAppointment(id) {
    const res = await apiFetch(`/api/appointments/${id}/status?status=COMPLETED`, { method: 'PATCH' });
    if (res.ok) {
        showToast('Appointment marked as COMPLETED');
        loadAppointments();
    }
}

async function markBillPaid(billId) {
    const res = await apiFetch(`/api/billing/bills/${billId}/pay?method=CARD`, { method: 'POST' });
    if (res.ok) {
        showToast('Invoice marked as PAID');
        loadBilling();
    }
}

// ========================================================
// UTILS & DROPDOWNS
// ========================================================
function populatePatientDropdowns(patients) {
    const opts = patients.map(p => `<option value="${p.id}">${p.firstName} ${p.lastName} (#${p.id})</option>`).join('');
    ['aptPatientId', 'rxPatientId'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.innerHTML = opts;
    });
}

function populateDoctorDropdowns(doctors) {
    const opts = doctors.map(d => `<option value="${d.id}">Dr. ${d.firstName} ${d.lastName} (${d.specialization})</option>`).join('');
    ['aptDoctorId', 'rxDoctorId'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.innerHTML = opts;
    });
}

function populateMedicineDropdowns(medicines) {
    const opts = medicines.map(m => `<option value="${m.id}">${m.name} ($${m.price})</option>`).join('');
    document.querySelectorAll('.rx-med-id').forEach(el => el.innerHTML = opts);
}

function calculateAge(dobString) {
    if (!dobString) return '-';
    const dob = new Date(dobString);
    const diff = Date.now() - dob.getTime();
    return Math.abs(new Date(diff).getUTCFullYear() - 1970);
}

function getStatusBadgeClass(status) {
    switch (status) {
        case 'SCHEDULED': return 'badge-info';
        case 'COMPLETED': return 'badge-success';
        case 'CANCELLED': return 'badge-danger';
        case 'IN_PROGRESS': return 'badge-warning';
        default: return 'badge-secondary';
    }
}

function openModal(id) {
    const el = document.getElementById(id);
    if (el) el.classList.remove('hidden');
}

function closeModal(id) {
    const el = document.getElementById(id);
    if (el) el.classList.add('hidden');
}

function showAlert(elemId, message, type) {
    const el = document.getElementById(elemId);
    if (el) {
        el.className = `alert alert-${type}`;
        el.innerText = message;
        el.classList.remove('hidden');
    }
}

function clearAlert(elemId) {
    const el = document.getElementById(elemId);
    if (el) el.classList.add('hidden');
}

function showToast(message) {
    const toast = document.getElementById('toastMessage');
    if (toast) {
        toast.innerText = message;
        toast.classList.remove('hidden');
        setTimeout(() => toast.classList.add('hidden'), 3500);
    }
}
