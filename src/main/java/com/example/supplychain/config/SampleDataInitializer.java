package com.example.supplychain.config;

import com.example.supplychain.model.*;
import com.example.supplychain.repository.*;
import com.example.supplychain.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SampleDataInitializer implements CommandLineRunner {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClusterRepository clusterRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private DeliveryService deliveryService;
    
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private VendorBillService vendorBillService;

    @Autowired
    private JournalEntryService journalEntryService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Prevent duplicate execution
        if (contactRepository.count() > 0) {
            System.out.println("Sample data already exists. Skipping initialization.");
            return;
        }

        System.out.println("Initializing sample data...");

        // 1. CONTACTS
        Contact lakshmi = createContact("Lakshmi Terracotta Crafts", "lakshmi.crafts@example.com", "9876543210", "Madurai, Tamil Nadu", ContactType.RURAL_ARTISAN);
        Contact kumar = createContact("Kumar Handloom Works", "kumar.handloom@example.com", "9876543211", "Kanchipuram, Tamil Nadu", ContactType.RURAL_ARTISAN);
        
        Contact greenLeaf = createContact("GreenLeaf Handicrafts", "greenleaf@example.com", "9876543212", "Coimbatore, Tamil Nadu", ContactType.MERCHANT_BUYER);
        Contact heritage = createContact("Heritage Home Store", "heritage@example.com", "9876543213", "Chennai, Tamil Nadu", ContactType.MERCHANT_BUYER);
        
        Contact fastTrack = createContact("FastTrack Logistics", "fasttrack@example.com", "9876543214", "Coimbatore, Tamil Nadu", ContactType.DELIVERY_PARTNER);
        Contact southIndia = createContact("South India Express", "southindia.express@example.com", "9876543215", "Chennai, Tamil Nadu", ContactType.DELIVERY_PARTNER);

        // 2. PRODUCTS
        // Initialize stock as 0. Purchase Orders will increase it.
        Product vase = createProduct("Handcrafted Terracotta Vase", "Handicrafts", new BigDecimal("700"), new BigDecimal("850"), lakshmi.getId());
        Product shawl = createProduct("Woven Silk Shawl", "Handloom", new BigDecimal("2100"), new BigDecimal("2500"), kumar.getId());
        Product deliveryServiceItem = createProduct("Last-Mile Delivery Service", "Logistics", new BigDecimal("300"), new BigDecimal("350"), fastTrack.getId());
        deliveryServiceItem.setProductType(ProductType.SERVICE);
        productRepository.save(deliveryServiceItem);

        // 3. ACCOUNTS
        Account cash = createAccount("1000", "Cash", AccountType.ASSET);
        Account stock = createAccount("1100", "Finished Goods Stock", AccountType.ASSET);
        Account payables = createAccount("2000", "Artisan Payables", AccountType.LIABILITY);
        Account revenue = createAccount("4000", "Handicraft Sales Revenue", AccountType.INCOME);
        Account cogs = createAccount("5000", "Artisan Cost of Goods", AccountType.EXPENSE);
        Account freight = createAccount("5100", "Freight Expenses", AccountType.EXPENSE);

        // 4. CLUSTERS (Analytic Accounts)
        Cluster southernCluster = new Cluster();
        southernCluster.setName("Southern Craft Cluster");
        southernCluster.setDescription("Regional cluster for Tamil Nadu rural artisan procurement and delivery operations.");
        southernCluster = clusterRepository.save(southernCluster);

        // 5. BUDGETS
        Budget budget1 = new Budget();
        budget1.setClusterId(southernCluster.getId());
        budget1.setPlannedAmount(new BigDecimal("100000"));
        budget1.setActualAmount(new BigDecimal("65000"));
        budgetRepository.save(budget1);

        Budget budget2 = new Budget();
        budget2.setClusterId(southernCluster.getId());
        budget2.setPlannedAmount(new BigDecimal("75000"));
        budget2.setActualAmount(new BigDecimal("42000"));
        budgetRepository.save(budget2);

        // 6. PURCHASE ORDERS
        PurchaseOrder po1 = new PurchaseOrder();
        po1.setArtisanId(lakshmi.getId());
        PurchaseOrderItem po1Item = new PurchaseOrderItem();
        po1Item.setProductId(vase.getId());
        po1Item.setQuantity(50);
        po1Item.setUnitPrice(vase.getPurchasePrice());
        po1.setItems(new ArrayList<>(List.of(po1Item)));
        po1 = purchaseOrderService.createPurchaseOrder(po1);
        purchaseOrderService.updateStatus(po1.getId(), OrderStatus.RECEIVED); // This increments stock by 50

        PurchaseOrder po2 = new PurchaseOrder();
        po2.setArtisanId(kumar.getId());
        PurchaseOrderItem po2Item = new PurchaseOrderItem();
        po2Item.setProductId(shawl.getId());
        po2Item.setQuantity(30);
        po2Item.setUnitPrice(shawl.getPurchasePrice());
        po2.setItems(new ArrayList<>(List.of(po2Item)));
        po2 = purchaseOrderService.createPurchaseOrder(po2);
        purchaseOrderService.updateStatus(po2.getId(), OrderStatus.RECEIVED); // This increments stock by 30

        // 7. SALES ORDERS
        SalesOrder so1 = new SalesOrder();
        so1.setMerchantId(greenLeaf.getId());
        SalesOrderItem so1Item = new SalesOrderItem();
        so1Item.setProductId(vase.getId());
        so1Item.setQuantity(5);
        so1Item.setUnitPrice(vase.getSellingPrice());
        so1.setItems(new ArrayList<>(List.of(so1Item)));
        so1 = salesOrderService.createSalesOrder(so1); // This decrements stock by 5

        SalesOrder so2 = new SalesOrder();
        so2.setMerchantId(heritage.getId());
        SalesOrderItem so2Item = new SalesOrderItem();
        so2Item.setProductId(shawl.getId());
        so2Item.setQuantity(3);
        so2Item.setUnitPrice(shawl.getSellingPrice());
        so2.setItems(new ArrayList<>(List.of(so2Item)));
        so2 = salesOrderService.createSalesOrder(so2);

        SalesOrder so3 = new SalesOrder();
        so3.setMerchantId(greenLeaf.getId());
        SalesOrderItem so3Item = new SalesOrderItem();
        so3Item.setProductId(vase.getId());
        so3Item.setQuantity(8);
        so3Item.setUnitPrice(vase.getSellingPrice());
        so3.setItems(new ArrayList<>(List.of(so3Item)));
        so3 = salesOrderService.createSalesOrder(so3);

        // 8. INVOICES
        Invoice inv1 = invoiceService.generateInvoiceFromOrder(so1.getId());
        Invoice inv2 = invoiceService.generateInvoiceFromOrder(so2.getId());
        // For so3, we might leave it without an invoice to show pending status, or we can generate one.
        
        // 9. PAYMENTS (Customer Payments)
        Payment pay1 = new Payment();
        pay1.setInvoiceId(inv1.getId());
        pay1.setAmount(inv1.getAmount());
        pay1.setPaymentType(PaymentType.BANK_TRANSFER);
        paymentService.processCustomerPayment(pay1);

        Payment pay2 = new Payment();
        pay2.setInvoiceId(inv2.getId());
        pay2.setAmount(inv2.getAmount());
        pay2.setPaymentType(PaymentType.CASH);
        paymentService.processCustomerPayment(pay2);

        // 10. DELIVERIES
        Delivery del1 = new Delivery();
        del1.setSalesOrderId(so1.getId());
        del1.setDeliveryPartnerId(fastTrack.getId());
        del1 = deliveryService.createDelivery(del1);
        deliveryService.updateStatus(del1.getId(), DeliveryStatus.ASSIGNED);
        deliveryService.updateStatus(del1.getId(), DeliveryStatus.DISPATCHED);
        deliveryService.updateStatus(del1.getId(), DeliveryStatus.IN_TRANSIT);
        deliveryService.updateStatus(del1.getId(), DeliveryStatus.DELIVERED);

        Delivery del2 = new Delivery();
        del2.setSalesOrderId(so2.getId());
        del2.setDeliveryPartnerId(southIndia.getId());
        del2 = deliveryService.createDelivery(del2);
        deliveryService.updateStatus(del2.getId(), DeliveryStatus.ASSIGNED);
        deliveryService.updateStatus(del2.getId(), DeliveryStatus.DISPATCHED);
        deliveryService.updateStatus(del2.getId(), DeliveryStatus.IN_TRANSIT);

        // 11. VENDOR BILLS
        VendorBill vb1 = new VendorBill();
        vb1.setPurchaseOrderId(po1.getId());
        vb1.setArtisanId(lakshmi.getId());
        vb1.setAmount(po1.getTotalAmount());
        vb1 = vendorBillService.createVendorBill(vb1);
        vendorBillService.payVendorBill(vb1.getId()); // sets PAID

        // 12. JOURNAL ENTRIES
        JournalEntry je1 = new JournalEntry();
        je1.setDescription("Sale of Terracotta Vase");
        je1.setReferenceType("INVOICE");
        je1.setReferenceId(inv1.getId());
        
        JournalLine line1 = new JournalLine();
        line1.setAccountId(cash.getId());
        line1.setDebit(inv1.getAmount());
        
        JournalLine line2 = new JournalLine();
        line2.setAccountId(revenue.getId());
        line2.setCredit(inv1.getAmount());
        
        je1.setLines(new ArrayList<>(List.of(line1, line2)));
        journalEntryService.createJournalEntry(je1);

        JournalEntry je2 = new JournalEntry();
        je2.setDescription("Cost of Goods Sold - Terracotta Vase");
        je2.setReferenceType("SALES_ORDER");
        je2.setReferenceId(so1.getId());
        
        JournalLine line3 = new JournalLine();
        line3.setAccountId(cogs.getId());
        line3.setDebit(new BigDecimal("3500")); // 5 qty * 700
        
        JournalLine line4 = new JournalLine();
        line4.setAccountId(stock.getId());
        line4.setCredit(new BigDecimal("3500"));
        
        je2.setLines(new ArrayList<>(List.of(line3, line4)));
        journalEntryService.createJournalEntry(je2);

        System.out.println("Sample data initialization complete.");
    }

    private Contact createContact(String name, String email, String phone, String address, ContactType type) {
        Contact contact = new Contact();
        contact.setName(name);
        contact.setEmail(email);
        contact.setPhone(phone);
        contact.setAddress(address);
        contact.setContactType(type);
        contact.setActive(true);
        return contactRepository.save(contact);
    }

    private Product createProduct(String name, String description, BigDecimal purchasePrice, BigDecimal sellingPrice, Long artisanId) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPurchasePrice(purchasePrice);
        product.setSellingPrice(sellingPrice);
        product.setStockQty(0);
        product.setProductType(ProductType.GOODS);
        product.setActive(true);
        product.setArtisanId(artisanId);
        return productRepository.save(product);
    }

    private Account createAccount(String code, String name, AccountType type) {
        Account account = new Account();
        account.setAccountCode(code);
        account.setAccountName(name);
        account.setAccountType(type);
        account.setBalance(BigDecimal.ZERO);
        return accountRepository.save(account);
    }
}
