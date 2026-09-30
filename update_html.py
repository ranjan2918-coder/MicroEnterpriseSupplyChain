import re

# Read index.html
with open('src/main/resources/static/index.html', 'r', encoding='utf-8') as f:
    html = f.read()

# Update nav links
html = html.replace('data-target="generic-section" data-title="Purchase Orders"', 'data-target="purchase-orders-section"')
html = html.replace('data-target="generic-section" data-title="Sales Orders"', 'data-target="sales-orders-section"')
html = html.replace('data-target="generic-section" data-title="Invoices"', 'data-target="invoices-section"')
html = html.replace('data-target="generic-section" data-title="Payments"', 'data-target="payments-section"')
html = html.replace('data-target="generic-section" data-title="Delivery Tracking"', 'data-target="delivery-tracking-section"')
html = html.replace('data-target="generic-section" data-title="Chart of Accounts"', 'data-target="chart-of-accounts-section"')
html = html.replace('data-target="generic-section" data-title="Analytic Accounts"', 'data-target="analytic-accounts-section"')
html = html.replace('data-target="generic-section" data-title="Budgets"', 'data-target="budgets-section"')
html = html.replace('data-target="generic-section" data-title="Reports"', 'data-target="reports-section"')

sections_html = """
                <!-- Purchase Orders Section -->
                <section id="purchase-orders-section" class="page-section">
                    <div class="page-header">
                        <h2>Purchase Orders</h2>
                        <button class="btn-primary" onclick="openPOModal()">Add Purchase Order</button>
                    </div>
                    <div class="card">
                        <div class="table-responsive">
                            <table class="data-table">
                                <thead>
                                    <tr><th>ID</th><th>Artisan ID</th><th>Order Date</th><th>Total Amount</th><th>Status</th><th>Actions</th></tr>
                                </thead>
                                <tbody id="po-table-body"><tr><td colspan="6" class="text-center">Loading...</td></tr></tbody>
                            </table>
                        </div>
                    </div>
                </section>

                <!-- Sales Orders Section -->
                <section id="sales-orders-section" class="page-section">
                    <div class="page-header">
                        <h2>Sales Orders</h2>
                        <button class="btn-primary" onclick="openSOModal()">Add Sales Order</button>
                    </div>
                    <div class="card">
                        <div class="table-responsive">
                            <table class="data-table">
                                <thead>
                                    <tr><th>ID</th><th>Merchant ID</th><th>Order Date</th><th>Total Amount</th><th>Status</th><th>Actions</th></tr>
                                </thead>
                                <tbody id="so-table-body"><tr><td colspan="6" class="text-center">Loading...</td></tr></tbody>
                            </table>
                        </div>
                    </div>
                </section>

                <!-- Invoices Section -->
                <section id="invoices-section" class="page-section">
                    <div class="page-header">
                        <h2>Invoices</h2>
                        <button class="btn-primary" onclick="openInvoiceModal()">Add Invoice</button>
                    </div>
                    <div class="card">
                        <div class="table-responsive">
                            <table class="data-table">
                                <thead>
                                    <tr><th>ID</th><th>Merchant ID</th><th>Sales Order ID</th><th>Amount</th><th>Date</th><th>Status</th></tr>
                                </thead>
                                <tbody id="invoice-table-body"><tr><td colspan="6" class="text-center">Loading...</td></tr></tbody>
                            </table>
                        </div>
                    </div>
                </section>

                <!-- Payments Section -->
                <section id="payments-section" class="page-section">
                    <div class="page-header">
                        <h2>Payments</h2>
                        <button class="btn-primary" onclick="openPaymentModal()">Record Payment</button>
                    </div>
                    <div class="card">
                        <div class="table-responsive">
                            <table class="data-table">
                                <thead>
                                    <tr><th>ID</th><th>Invoice / Bill ID</th><th>Amount</th><th>Date</th><th>Type</th><th>Status</th></tr>
                                </thead>
                                <tbody id="payment-table-body"><tr><td colspan="6" class="text-center">Loading...</td></tr></tbody>
                            </table>
                        </div>
                    </div>
                </section>

                <!-- Delivery Tracking Section -->
                <section id="delivery-tracking-section" class="page-section">
                    <div class="page-header">
                        <h2>Delivery Tracking</h2>
                        <button class="btn-primary" onclick="openDeliveryModal()">Add Delivery</button>
                    </div>
                    <div class="card">
                        <div class="table-responsive">
                            <table class="data-table">
                                <thead>
                                    <tr><th>ID</th><th>Sales Order ID</th><th>Partner ID</th><th>Tracking #</th><th>Status</th><th>Date</th></tr>
                                </thead>
                                <tbody id="delivery-table-body"><tr><td colspan="6" class="text-center">Loading...</td></tr></tbody>
                            </table>
                        </div>
                    </div>
                </section>

                <!-- Chart of Accounts Section -->
                <section id="chart-of-accounts-section" class="page-section">
                    <div class="page-header">
                        <h2>Chart of Accounts</h2>
                        <button class="btn-primary" onclick="openAccountModal()">Add Account</button>
                    </div>
                    <div class="card">
                        <div class="table-responsive">
                            <table class="data-table">
                                <thead>
                                    <tr><th>ID</th><th>Code</th><th>Name</th><th>Type</th><th>Balance</th></tr>
                                </thead>
                                <tbody id="account-table-body"><tr><td colspan="5" class="text-center">Loading...</td></tr></tbody>
                            </table>
                        </div>
                    </div>
                </section>

                <!-- Analytic Accounts Section -->
                <section id="analytic-accounts-section" class="page-section">
                    <div class="page-header">
                        <h2>Analytic Accounts (Clusters)</h2>
                        <button class="btn-primary" onclick="openClusterModal()">Add Cluster</button>
                    </div>
                    <div class="card">
                        <div class="table-responsive">
                            <table class="data-table">
                                <thead>
                                    <tr><th>ID</th><th>Name</th><th>Region</th><th>Description</th></tr>
                                </thead>
                                <tbody id="cluster-table-body"><tr><td colspan="4" class="text-center">Loading...</td></tr></tbody>
                            </table>
                        </div>
                    </div>
                </section>

                <!-- Budgets Section -->
                <section id="budgets-section" class="page-section">
                    <div class="page-header">
                        <h2>Budgets</h2>
                        <button class="btn-primary" onclick="openBudgetModal()">Add Budget</button>
                    </div>
                    <div class="card">
                        <div class="table-responsive">
                            <table class="data-table">
                                <thead>
                                    <tr><th>ID</th><th>Year</th><th>Cluster ID</th><th>Planned</th><th>Actual</th><th>Remaining</th></tr>
                                </thead>
                                <tbody id="budget-table-body"><tr><td colspan="6" class="text-center">Loading...</td></tr></tbody>
                            </table>
                        </div>
                    </div>
                </section>

                <!-- Reports Section -->
                <section id="reports-section" class="page-section">
                    <div class="page-header">
                        <h2>Reports Dashboard</h2>
                    </div>
                    <div class="stats-grid">
                        <div class="card">
                            <h3>Profit & Loss</h3>
                            <div id="report-pl">Loading...</div>
                        </div>
                        <div class="card">
                            <h3>Balance Sheet</h3>
                            <div id="report-bs">Loading...</div>
                        </div>
                    </div>
                </section>
"""

# Replace generic-section
html = re.sub(r'<!-- Generic Section -->.*?</section>', sections_html, html, flags=re.DOTALL)

# Add modals
modals_html = """
    <!-- PO Modal -->
    <div id="poModal" class="modal">
        <div class="modal-content">
            <div class="modal-header"><h2>Create Purchase Order</h2><span class="close" onclick="closeModal('poModal')">&times;</span></div>
            <div class="modal-body">
                <form id="poForm" onsubmit="handlePOSubmit(event)">
                    <div class="form-group"><label>Artisan ID</label><input type="number" id="poArtisanId" required></div>
                    <div class="form-group"><label>Product ID</label><input type="number" id="poProductId" required></div>
                    <div class="form-group"><label>Quantity</label><input type="number" id="poQty" required></div>
                    <div class="form-group"><label>Unit Price</label><input type="number" step="0.01" id="poPrice" required></div>
                    <div class="form-actions"><button type="button" class="btn-secondary" onclick="closeModal('poModal')">Cancel</button><button type="submit" class="btn-primary">Create</button></div>
                </form>
            </div>
        </div>
    </div>
    <!-- SO Modal -->
    <div id="soModal" class="modal">
        <div class="modal-content">
            <div class="modal-header"><h2>Create Sales Order</h2><span class="close" onclick="closeModal('soModal')">&times;</span></div>
            <div class="modal-body">
                <form id="soForm" onsubmit="handleSOSubmit(event)">
                    <div class="form-group"><label>Merchant ID</label><input type="number" id="soMerchantId" required></div>
                    <div class="form-group"><label>Product ID</label><input type="number" id="soProductId" required></div>
                    <div class="form-group"><label>Quantity</label><input type="number" id="soQty" required></div>
                    <div class="form-group"><label>Unit Price</label><input type="number" step="0.01" id="soPrice" required></div>
                    <div class="form-actions"><button type="button" class="btn-secondary" onclick="closeModal('soModal')">Cancel</button><button type="submit" class="btn-primary">Create</button></div>
                </form>
            </div>
        </div>
    </div>
    <!-- Invoice Modal -->
    <div id="invoiceModal" class="modal">
        <div class="modal-content">
            <div class="modal-header"><h2>Create Invoice</h2><span class="close" onclick="closeModal('invoiceModal')">&times;</span></div>
            <div class="modal-body">
                <form id="invoiceForm" onsubmit="handleInvoiceSubmit(event)">
                    <div class="form-group"><label>Sales Order ID</label><input type="number" id="invOrderId" required></div>
                    <div class="form-actions"><button type="button" class="btn-secondary" onclick="closeModal('invoiceModal')">Cancel</button><button type="submit" class="btn-primary">Create</button></div>
                </form>
            </div>
        </div>
    </div>
    <!-- Payment Modal -->
    <div id="paymentModal" class="modal">
        <div class="modal-content">
            <div class="modal-header"><h2>Record Payment</h2><span class="close" onclick="closeModal('paymentModal')">&times;</span></div>
            <div class="modal-body">
                <form id="paymentForm" onsubmit="handlePaymentSubmit(event)">
                    <div class="form-group"><label>Invoice ID</label><input type="number" id="payInvoiceId" required></div>
                    <div class="form-group"><label>Amount</label><input type="number" step="0.01" id="payAmount" required></div>
                    <div class="form-group"><label>Reference Number</label><input type="text" id="payRef"></div>
                    <div class="form-actions"><button type="button" class="btn-secondary" onclick="closeModal('paymentModal')">Cancel</button><button type="submit" class="btn-primary">Create</button></div>
                </form>
            </div>
        </div>
    </div>
    <!-- Delivery Modal -->
    <div id="deliveryModal" class="modal">
        <div class="modal-content">
            <div class="modal-header"><h2>Add Delivery</h2><span class="close" onclick="closeModal('deliveryModal')">&times;</span></div>
            <div class="modal-body">
                <form id="deliveryForm" onsubmit="handleDeliverySubmit(event)">
                    <div class="form-group"><label>Sales Order ID</label><input type="number" id="delOrderId" required></div>
                    <div class="form-group"><label>Partner ID</label><input type="number" id="delPartnerId" required></div>
                    <div class="form-group"><label>Tracking Number</label><input type="text" id="delTracking" required></div>
                    <div class="form-actions"><button type="button" class="btn-secondary" onclick="closeModal('deliveryModal')">Cancel</button><button type="submit" class="btn-primary">Create</button></div>
                </form>
            </div>
        </div>
    </div>
    <!-- Account Modal -->
    <div id="accountModal" class="modal">
        <div class="modal-content">
            <div class="modal-header"><h2>Add Account</h2><span class="close" onclick="closeModal('accountModal')">&times;</span></div>
            <div class="modal-body">
                <form id="accountForm" onsubmit="handleAccountSubmit(event)">
                    <div class="form-group"><label>Account Code</label><input type="text" id="accCode" required></div>
                    <div class="form-group"><label>Account Name</label><input type="text" id="accName" required></div>
                    <div class="form-group"><label>Type</label><select id="accType" required><option value="ASSET">Asset</option><option value="LIABILITY">Liability</option><option value="INCOME">Income</option><option value="EXPENSE">Expense</option></select></div>
                    <div class="form-actions"><button type="button" class="btn-secondary" onclick="closeModal('accountModal')">Cancel</button><button type="submit" class="btn-primary">Create</button></div>
                </form>
            </div>
        </div>
    </div>
    <!-- Cluster Modal -->
    <div id="clusterModal" class="modal">
        <div class="modal-content">
            <div class="modal-header"><h2>Add Cluster</h2><span class="close" onclick="closeModal('clusterModal')">&times;</span></div>
            <div class="modal-body">
                <form id="clusterForm" onsubmit="handleClusterSubmit(event)">
                    <div class="form-group"><label>Name</label><input type="text" id="cluName" required></div>
                    <div class="form-group"><label>Region</label><input type="text" id="cluRegion" required></div>
                    <div class="form-group"><label>Description</label><input type="text" id="cluDesc" required></div>
                    <div class="form-actions"><button type="button" class="btn-secondary" onclick="closeModal('clusterModal')">Cancel</button><button type="submit" class="btn-primary">Create</button></div>
                </form>
            </div>
        </div>
    </div>
    <!-- Budget Modal -->
    <div id="budgetModal" class="modal">
        <div class="modal-content">
            <div class="modal-header"><h2>Add Budget</h2><span class="close" onclick="closeModal('budgetModal')">&times;</span></div>
            <div class="modal-body">
                <form id="budgetForm" onsubmit="handleBudgetSubmit(event)">
                    <div class="form-group"><label>Budget Year</label><input type="number" id="budYear" required></div>
                    <div class="form-group"><label>Cluster ID</label><input type="number" id="budClusterId" required></div>
                    <div class="form-group"><label>Planned Amount</label><input type="number" step="0.01" id="budPlanned" required></div>
                    <div class="form-actions"><button type="button" class="btn-secondary" onclick="closeModal('budgetModal')">Cancel</button><button type="submit" class="btn-primary">Create</button></div>
                </form>
            </div>
        </div>
    </div>
"""

html = html.replace('<script src="script.js"></script>', modals_html + '\n    <script src="script.js"></script>')

with open('src/main/resources/static/index.html', 'w', encoding='utf-8') as f:
    f.write(html)
