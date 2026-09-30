document.addEventListener('DOMContentLoaded', () => {
    initializeNavigation();
    showSection('dashboard-section');
    loadDashboard();
});

// --- API Wrapper ---
const API_BASE = '/api';

async function apiRequest(url, options = {}) {
    try {
        const response = await fetch(API_BASE + url, {
            headers: {
                "Content-Type": "application/json"
            },
            ...options
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error("API Error:", url, error);
        throw error;
    }
}

// --- Navigation ---
function initializeNavigation() {
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const targetId = link.getAttribute('data-target');
            const title = link.getAttribute('data-title');
            showSection(targetId, title);
        });
    });
}

function showSection(sectionId, title = null) {
    const sections = document.querySelectorAll('.page-section');
    const navLinks = document.querySelectorAll('.nav-link');
    const genericTitle = document.getElementById('generic-title');

    // Hide all sections
    sections.forEach(section => section.classList.remove('active'));
    
    // Update active nav link
    navLinks.forEach(nav => {
        nav.classList.remove('active');
        if (nav.getAttribute('data-target') === sectionId) {
            nav.classList.add('active');
        }
    });

    // Show selected section
    const targetSection = document.getElementById(sectionId);
    if (targetSection) {
        targetSection.classList.add('active');
    }

    if (sectionId === 'generic-section' && title) {
        genericTitle.textContent = title;
    }

    // Load data
    if (sectionId === 'dashboard-section') loadDashboard();
    else if (sectionId === 'contacts-section') loadContacts();
    else if (sectionId === 'products-section') loadProducts();
    else if (sectionId === 'purchase-orders-section') loadPOs();
    else if (sectionId === 'sales-orders-section') loadSOs();
    else if (sectionId === 'invoices-section') loadInvoices();
    else if (sectionId === 'payments-section') loadPayments();
    else if (sectionId === 'delivery-tracking-section') loadDeliveries();
    else if (sectionId === 'chart-of-accounts-section') loadAccounts();
    else if (sectionId === 'analytic-accounts-section') loadClusters();
    else if (sectionId === 'budgets-section') loadBudgets();
    else if (sectionId === 'reports-section') loadReports();
}

// --- Dashboard ---
async function loadDashboard() {
    const statContacts = document.getElementById('stat-contacts');
    const statProducts = document.getElementById('stat-products');
    const statOrders = document.getElementById('stat-orders');
    const statPending = document.getElementById('stat-pending');

    try {
        const contacts = await apiRequest('/contacts');
        statContacts.textContent = contacts.length;
    } catch (e) {
        statContacts.textContent = 'Unavailable';
    }

    try {
        const products = await apiRequest('/products');
        statProducts.textContent = products.length;
    } catch (e) {
        statProducts.textContent = 'Unavailable';
    }

    try {
        const pOrders = await apiRequest('/purchase-orders');
        const sOrders = await apiRequest('/orders'); // Actually SalesOrders uses /api/orders
        
        const totalOrders = pOrders.length + sOrders.length;
        statOrders.textContent = totalOrders;
        
        const pendingP = pOrders.filter(o => o.status === 'PENDING').length;
        const pendingS = sOrders.filter(o => o.status === 'PENDING').length;
        statPending.textContent = pendingP + pendingS;
    } catch (e) {
        statOrders.textContent = 'Unavailable';
        statPending.textContent = 'Unavailable';
    }
}

// --- Contacts ---
async function loadContacts() {
    const tbody = document.getElementById('contacts-table-body');
    tbody.innerHTML = '<tr><td colspan="6" class="text-center">Loading...</td></tr>';
    
    try {
        const contacts = await apiRequest('/contacts');
        if (contacts.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center">No contacts found</td></tr>';
            return;
        }

        tbody.innerHTML = contacts.map(c => `
            <tr>
                <td>${c.id}</td>
                <td>${c.name}</td>
                <td>${c.email}</td>
                <td>${c.phone}</td>
                <td><span class="badge">${c.contactType}</span></td>
                <td>
                    <button class="btn-danger" onclick="deleteContact(${c.id})"><i class="fas fa-trash"></i></button>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-red">Failed to load contacts</td></tr>';
    }
}

function openContactModal() {
    document.getElementById('contactForm').reset();
    document.getElementById('contactModal').style.display = 'block';
}

function closeModal(id) {
    document.getElementById(id).style.display = 'none';
}

async function handleContactSubmit(e) {
    e.preventDefault();
    const data = {
        name: document.getElementById('contactName').value,
        email: document.getElementById('contactEmail').value,
        phone: document.getElementById('contactPhone').value,
        address: document.getElementById('contactAddress').value,
        contactType: document.getElementById('contactType').value,
        active: true
    };

    try {
        await apiRequest('/contacts', {
            method: 'POST',
            body: JSON.stringify(data)
        });
        closeModal('contactModal');
        loadContacts();
    } catch (e) {
        alert('Failed to save contact.');
    }
}

async function deleteContact(id) {
    if (confirm('Are you sure you want to delete this contact?')) {
        try {
            await apiRequest(`/contacts/${id}`, { method: 'DELETE' });
            loadContacts();
        } catch (e) {
            alert('Failed to delete contact.');
        }
    }
}

// --- Products ---
async function loadProducts() {
    const tbody = document.getElementById('products-table-body');
    tbody.innerHTML = '<tr><td colspan="6" class="text-center">Loading...</td></tr>';
    
    try {
        const products = await apiRequest('/products');
        if (products.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center">No products found</td></tr>';
            return;
        }

        tbody.innerHTML = products.map(p => `
            <tr>
                <td>${p.id}</td>
                <td>${p.name}</td>
                <td><span class="badge">${p.productType}</span></td>
                <td>$${p.sellingPrice}</td>
                <td>${p.stockQty || 0}</td>
                <td>
                    <button class="btn-danger" onclick="deleteProduct(${p.id})"><i class="fas fa-trash"></i></button>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-red">Failed to load products</td></tr>';
    }
}

async function deleteProduct(id) {
    if (confirm('Are you sure you want to delete this product?')) {
        try {
            await apiRequest(`/products/${id}`, { method: 'DELETE' });
            loadProducts();
        } catch (e) {
            alert('Failed to delete product.');
        }
    }
}

// Close modals when clicking outside
window.onclick = function(event) {
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    });
}


// --- Purchase Orders ---
async function loadPOs() {
    const tbody = document.getElementById('po-table-body');
    tbody.innerHTML = '<tr><td colspan="6" class="text-center">Loading...</td></tr>';
    try {
        const data = await apiRequest('/purchase-orders');
        if (data.length === 0) return tbody.innerHTML = '<tr><td colspan="6" class="text-center">No purchase orders found</td></tr>';
        tbody.innerHTML = data.map(i => `<tr><td>${i.id}</td><td>${i.artisanId}</td><td>${new Date(i.orderDate).toLocaleDateString()}</td><td>$${i.totalAmount || 0}</td><td><span class="badge">${i.status}</span></td><td>
            <button class="btn-primary" onclick="updatePOStatus(${i.id})">Update Status</button>
        </td></tr>`).join('');
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-red">Failed to load</td></tr>';
    }
}
function openPOModal() { document.getElementById('poForm').reset(); document.getElementById('poModal').style.display = 'block'; }
async function handlePOSubmit(e) {
    e.preventDefault();
    const payload = { artisanId: parseInt(document.getElementById('poArtisanId').value), items: [{ productId: parseInt(document.getElementById('poProductId').value), quantity: parseInt(document.getElementById('poQty').value), unitPrice: parseFloat(document.getElementById('poPrice').value) }] };
    try { await apiRequest('/purchase-orders', { method: 'POST', body: JSON.stringify(payload) }); closeModal('poModal'); loadPOs(); alert('Purchase Order created successfully.'); loadDashboard(); } catch (e) { alert('Failed to save.'); }
}
async function updatePOStatus(id) {
    const status = prompt("Enter new status (CREATED, APPROVED, RECEIVED, CANCELLED):");
    if(status) {
        try { await apiRequest(`/purchase-orders/${id}/status?status=${status}`, { method: 'PUT' }); loadPOs(); } catch (e) { alert('Failed to update status'); }
    }
}

// --- Sales Orders ---
async function loadSOs() {
    const tbody = document.getElementById('so-table-body');
    tbody.innerHTML = '<tr><td colspan="6" class="text-center">Loading...</td></tr>';
    try {
        const data = await apiRequest('/orders');
        if (data.length === 0) return tbody.innerHTML = '<tr><td colspan="6" class="text-center">No sales orders found</td></tr>';
        tbody.innerHTML = data.map(i => `<tr><td>${i.id}</td><td>${i.merchantId}</td><td>${new Date(i.orderDate).toLocaleDateString()}</td><td>$${i.totalAmount || 0}</td><td><span class="badge">${i.status}</span></td><td>
            <button class="btn-primary" onclick="updateSOStatus(${i.id})">Update Status</button>
        </td></tr>`).join('');
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-red">Failed to load</td></tr>';
    }
}
function openSOModal() { document.getElementById('soForm').reset(); document.getElementById('soModal').style.display = 'block'; }
async function handleSOSubmit(e) {
    e.preventDefault();
    const payload = { merchantId: parseInt(document.getElementById('soMerchantId').value), items: [{ productId: parseInt(document.getElementById('soProductId').value), quantity: parseInt(document.getElementById('soQty').value), unitPrice: parseFloat(document.getElementById('soPrice').value) }] };
    try { await apiRequest('/orders', { method: 'POST', body: JSON.stringify(payload) }); closeModal('soModal'); loadSOs(); alert('Sales Order created successfully.'); loadDashboard(); } catch (e) { alert('Failed to save.'); }
}
async function updateSOStatus(id) {
    const status = prompt("Enter new status (CREATED, CONFIRMED, FULFILLED, CANCELLED):");
    if(status) {
        try { await apiRequest(`/orders/${id}/status?status=${status}`, { method: 'PUT' }); loadSOs(); } catch (e) { alert('Failed to update status'); }
    }
}

// --- Invoices ---
async function loadInvoices() {
    const tbody = document.getElementById('invoice-table-body');
    tbody.innerHTML = '<tr><td colspan="6" class="text-center">Loading...</td></tr>';
    try {
        const data = await apiRequest('/invoices');
        if (data.length === 0) return tbody.innerHTML = '<tr><td colspan="6" class="text-center">No invoices found</td></tr>';
        tbody.innerHTML = data.map(i => `<tr><td>${i.id}</td><td>${i.merchantId}</td><td>${i.salesOrderId}</td><td>$${i.amount}</td><td>${new Date(i.invoiceDate).toLocaleDateString()}</td><td><span class="badge">${i.status}</span></td></tr>`).join('');
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-red">Failed to load</td></tr>';
    }
}
function openInvoiceModal() { document.getElementById('invoiceForm').reset(); document.getElementById('invoiceModal').style.display = 'block'; }
async function handleInvoiceSubmit(e) {
    e.preventDefault();
    const orderId = document.getElementById('invOrderId').value;
    try { await apiRequest(`/invoices/from-order/${orderId}`, { method: 'POST' }); closeModal('invoiceModal'); loadInvoices(); alert('Invoice created successfully.'); } catch (e) { alert('Failed to create invoice.'); }
}

// --- Payments ---
async function loadPayments() {
    const tbody = document.getElementById('payment-table-body');
    tbody.innerHTML = '<tr><td colspan="6" class="text-center">Loading...</td></tr>';
    try {
        const data = await apiRequest('/payments');
        if (data.length === 0) return tbody.innerHTML = '<tr><td colspan="6" class="text-center">No payments found</td></tr>';
        tbody.innerHTML = data.map(i => `<tr><td>${i.id}</td><td>${i.invoiceId || i.vendorBillId || '-'}</td><td>$${i.amount}</td><td>${new Date(i.paymentDate).toLocaleDateString()}</td><td>${i.paymentType}</td><td><span class="badge">${i.status}</span></td></tr>`).join('');
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-red">Failed to load</td></tr>';
    }
}
function openPaymentModal() { document.getElementById('paymentForm').reset(); document.getElementById('paymentModal').style.display = 'block'; }
async function handlePaymentSubmit(e) {
    e.preventDefault();
    const payload = { invoiceId: parseInt(document.getElementById('payInvoiceId').value), amount: parseFloat(document.getElementById('payAmount').value), referenceNumber: document.getElementById('payRef').value, paymentType: 'BANK_TRANSFER' };
    try { await apiRequest('/payments/customer', { method: 'POST', body: JSON.stringify(payload) }); closeModal('paymentModal'); loadPayments(); alert('Payment recorded successfully.'); } catch (e) { alert('Failed to record.'); }
}

// --- Delivery Tracking ---
async function loadDeliveries() {
    const tbody = document.getElementById('delivery-table-body');
    tbody.innerHTML = '<tr><td colspan="6" class="text-center">Loading...</td></tr>';
    try {
        const data = await apiRequest('/deliveries');
        if (data.length === 0) return tbody.innerHTML = '<tr><td colspan="6" class="text-center">No deliveries found</td></tr>';
        tbody.innerHTML = data.map(i => `<tr><td>${i.id}</td><td>${i.salesOrderId}</td><td>${i.deliveryPartnerId}</td><td>${i.trackingNumber}</td><td><span class="badge">${i.status}</span></td><td>${i.deliveryDate ? new Date(i.deliveryDate).toLocaleDateString() : '-'}</td></tr>`).join('');
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-red">Failed to load</td></tr>';
    }
}
function openDeliveryModal() { document.getElementById('deliveryForm').reset(); document.getElementById('deliveryModal').style.display = 'block'; }
async function handleDeliverySubmit(e) {
    e.preventDefault();
    const payload = { salesOrderId: parseInt(document.getElementById('delOrderId').value), deliveryPartnerId: parseInt(document.getElementById('delPartnerId').value), trackingNumber: document.getElementById('delTracking').value };
    try { await apiRequest('/deliveries', { method: 'POST', body: JSON.stringify(payload) }); closeModal('deliveryModal'); loadDeliveries(); alert('Delivery tracked successfully.'); } catch (e) { alert('Failed to save.'); }
}

// --- Chart of Accounts ---
async function loadAccounts() {
    const tbody = document.getElementById('account-table-body');
    tbody.innerHTML = '<tr><td colspan="5" class="text-center">Loading...</td></tr>';
    try {
        const data = await apiRequest('/accounts');
        if (data.length === 0) return tbody.innerHTML = '<tr><td colspan="5" class="text-center">No accounts found</td></tr>';
        tbody.innerHTML = data.map(i => `<tr><td>${i.id}</td><td>${i.accountCode}</td><td>${i.accountName}</td><td><span class="badge">${i.accountType}</span></td><td>$${i.balance}</td></tr>`).join('');
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center text-red">Failed to load</td></tr>';
    }
}
function openAccountModal() { document.getElementById('accountForm').reset(); document.getElementById('accountModal').style.display = 'block'; }
async function handleAccountSubmit(e) {
    e.preventDefault();
    const payload = { accountCode: document.getElementById('accCode').value, accountName: document.getElementById('accName').value, accountType: document.getElementById('accType').value, balance: 0.00 };
    try { await apiRequest('/accounts', { method: 'POST', body: JSON.stringify(payload) }); closeModal('accountModal'); loadAccounts(); alert('Account created successfully.'); } catch (e) { alert('Failed to save.'); }
}

// --- Analytic Accounts ---
async function loadClusters() {
    const tbody = document.getElementById('cluster-table-body');
    tbody.innerHTML = '<tr><td colspan="4" class="text-center">Loading...</td></tr>';
    try {
        const data = await apiRequest('/clusters');
        if (data.length === 0) return tbody.innerHTML = '<tr><td colspan="4" class="text-center">No clusters found</td></tr>';
        tbody.innerHTML = data.map(i => `<tr><td>${i.id}</td><td>${i.name}</td><td>${i.region}</td><td>${i.description}</td></tr>`).join('');
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center text-red">Failed to load</td></tr>';
    }
}
function openClusterModal() { document.getElementById('clusterForm').reset(); document.getElementById('clusterModal').style.display = 'block'; }
async function handleClusterSubmit(e) {
    e.preventDefault();
    const payload = { name: document.getElementById('cluName').value, region: document.getElementById('cluRegion').value, description: document.getElementById('cluDesc').value };
    try { await apiRequest('/clusters', { method: 'POST', body: JSON.stringify(payload) }); closeModal('clusterModal'); loadClusters(); alert('Cluster created successfully.'); } catch (e) { alert('Failed to save.'); }
}

// --- Budgets ---
async function loadBudgets() {
    const tbody = document.getElementById('budget-table-body');
    tbody.innerHTML = '<tr><td colspan="6" class="text-center">Loading...</td></tr>';
    try {
        const data = await apiRequest('/budgets');
        if (data.length === 0) return tbody.innerHTML = '<tr><td colspan="6" class="text-center">No budgets found</td></tr>';
        tbody.innerHTML = data.map(i => {
            const planned = i.plannedAmount || 0;
            const actual = i.actualAmount || 0;
            const remaining = planned - actual;
            return `<tr><td>${i.id}</td><td>${i.budgetYear}</td><td>${i.clusterId}</td><td>$${planned}</td><td>$${actual}</td><td>$${remaining}</td></tr>`;
        }).join('');
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-red">Failed to load</td></tr>';
    }
}
function openBudgetModal() { document.getElementById('budgetForm').reset(); document.getElementById('budgetModal').style.display = 'block'; }
async function handleBudgetSubmit(e) {
    e.preventDefault();
    const payload = { budgetYear: parseInt(document.getElementById('budYear').value), clusterId: parseInt(document.getElementById('budClusterId').value), plannedAmount: parseFloat(document.getElementById('budPlanned').value), actualAmount: 0.00 };
    try { await apiRequest('/budgets', { method: 'POST', body: JSON.stringify(payload) }); closeModal('budgetModal'); loadBudgets(); alert('Budget created successfully.'); } catch (e) { alert('Failed to save.'); }
}

// --- Reports ---
async function loadReports() {
    const pl = document.getElementById('report-pl');
    const bs = document.getElementById('report-bs');
    
    pl.innerHTML = 'Loading...';
    bs.innerHTML = 'Loading...';

    try {
        const data = await apiRequest('/reports/profit-loss');
        pl.innerHTML = `
            <p><strong>Total Revenue:</strong> $${data.totalRevenue}</p>
            <p><strong>Total Expenses:</strong> $${data.totalExpenses}</p>
            <p><strong>Net Income:</strong> $${data.netIncome}</p>
        `;
    } catch (e) {
        pl.innerHTML = '<span class="text-red">Report API not available</span>';
    }
    
    try {
        const data = await apiRequest('/reports/balance-sheet');
        bs.innerHTML = `
            <p><strong>Total Assets:</strong> $${data.totalAssets}</p>
            <p><strong>Total Liabilities:</strong> $${data.totalLiabilities}</p>
            <p><strong>Equity:</strong> $${data.equity}</p>
        `;
    } catch (e) {
        bs.innerHTML = '<span class="text-red">Report API not available</span>';
    }
}
