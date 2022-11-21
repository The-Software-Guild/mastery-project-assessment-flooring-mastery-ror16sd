package service;
import com.sg.flooring.dao.FlooringAuditDaoImpl;
import com.sg.flooring.dao.FlooringDao;
import com.sg.flooring.dao.FlooringDaoFileImpl;
import com.sg.flooring.dto.Order;
import com.sg.flooring.dto.Product;
import com.sg.flooring.dto.StateTax;
import com.sg.flooring.service.FlooringPersistenceException;
import com.sg.flooring.service.FlooringServiceLayer;
import com.sg.flooring.service.FlooringServiceLayerImpl;
import org.junit.jupiter.api.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FlooringServiceLayerImplTest {
    public static FlooringServiceLayer service;
    public static FlooringDao testdao;
    public FlooringServiceLayerImplTest(){
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        service = ctx.getBean("serviceLayer", FlooringServiceLayer.class);
    }

    @BeforeAll
    public static void setUpClass() throws Exception, FlooringPersistenceException {
        String productFile = "Data/Products.txt";
        String stateTaxFile = "Data/Taxes.txt";
        testdao = new FlooringDaoFileImpl(productFile,stateTaxFile);
        service = new FlooringServiceLayerImpl(testdao, new FlooringAuditDaoImpl());
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() throws FlooringPersistenceException {
        service = new FlooringServiceLayerImpl(new FlooringDaoFileImpl("Data/Products.txt","Data/Taxes.txt"), new FlooringAuditDaoImpl());
    }

    @AfterEach
    public void tearDown() {
    }

    @org.junit.jupiter.api.Test
    public void testGetOrder() throws Exception{


        service.loadProductAndTaxInfo();
        List<Product> productList = service.listAllProducts();

        String orderNumber = "1";
        String customerName = "testGetOrder";

        Product product = productList.get(2);
        StateTax stateInfo = new StateTax("CA",new BigDecimal("25.00").setScale(2, RoundingMode.FLOOR));
        com.sg.flooring.dto.Order order =  new com.sg.flooring.dto.Order(Integer.parseInt(orderNumber),"testGetOrder",stateInfo, product);
        order.setCustomerName("TESTGETORDER");

        String date = "02/02/2023";
        String name = order.getCustomerName();
        String state = order.getState();
        String type = order.getProductType();
        BigDecimal area = order.getArea();

        service.writeOrderToFile(date,order);
        service.listAllOrders("02022023");

        com.sg.flooring.dto.Order retrievedOrder = service.getOrder(orderNumber);
        assertNotNull(retrievedOrder,"Object not null");
        assertEquals(order.getOrderNumber(),retrievedOrder.getOrderNumber(), "Checking order number");
        assertEquals(order.getCustomerName(),retrievedOrder.getCustomerName(), "Checking customer name");
        assertEquals(order.getProductType(), retrievedOrder.getProductType(), "Checking product type");
        assertEquals(order.getArea(), retrievedOrder.getArea(), "Checking area");
        assertEquals(order.getState(), retrievedOrder.getState(), "Checking state");
        assertEquals(order.getTaxRate(), retrievedOrder.getTaxRate(), "Checking tax rate");
        assertEquals(order.getTotal(), retrievedOrder.getTotal(), "Checking total");

        service.removeOrder(date,orderNumber);
    }

    @Test
    public void testListAllOrders() throws Exception{


        service.loadProductAndTaxInfo();
        List<Product> productList = service.listAllProducts();

        String orderNumber = "1";
        String customerName = "testlistall";

        Product product = productList.get(3);
        StateTax stateInfo = new StateTax("CA",new BigDecimal("25.00").setScale(2, RoundingMode.FLOOR));
        Order order1 =  new Order(Integer.parseInt(orderNumber),"testGetOrder",stateInfo, product);
        order1.setCustomerName("TESTLISTALL-1");

        String date = "02/02/2023";
        String name = order1.getCustomerName();
        String state = order1.getState();
        String type = order1.getProductType();
        BigDecimal area = order1.getArea();


        String orderNumber2 = "2";
        String customerName2 = "testlistall2";

        Product product2 = productList.get(1);
        StateTax stateInfo2 = new StateTax("WA",new BigDecimal("9.25").setScale(2, RoundingMode.FLOOR));
        Order order2 =  new Order(Integer.parseInt(orderNumber2),"testGetOrder",stateInfo2, product2);
        order2.setCustomerName("TESTLISTALL-2");

        String name2 = order2.getCustomerName();
        String state2 = order2.getState();
        String type2 = order2.getProductType();
        BigDecimal area2 = order2.getArea();


        String orderNumber3 = "3";
        String customerName3 = "testlistall3";

        Product product3 = productList.get(3);
        StateTax stateInfo3 = new StateTax("WA",new BigDecimal("9.25").setScale(2, RoundingMode.FLOOR));
        Order order3 =  new Order(Integer.parseInt(orderNumber3),"testGetOrder",stateInfo3, product3);
        order3.setCustomerName("TESTLISTALL-3");

        String name3 = order3.getCustomerName();
        String state3 = order3.getState();
        String type3 = order3.getProductType();
        BigDecimal area3 = order3.getArea();

        //add the Orders to the file
        service.writeOrderToFile(date,order1);
        service.writeOrderToFile(date,order2);
        service.writeOrderToFile(date,order3);

        //load and capture all the orders under specified date
        List<Order> testList = service.listAllOrders("02022023");

        Order retrievedOrder1 = service.getOrder("1");
        Order retrievedOrder2 = service.getOrder("2");
        Order retrievedOrder3 = service.getOrder("3");

        assertNotNull(testList, "list of orders is not null");
        assertEquals(3, testList.size(), "List of orders should be 3");
        assertTrue(testList.contains(retrievedOrder1), "list contains retrievedOrder1");
        assertTrue(testList.contains(retrievedOrder2), "list contains retrievedOrder2");
        assertTrue(testList.contains(retrievedOrder3), "list contains retrievedOrder3");

        //empty list and file just encase
        service.removeOrder(date,"3");
        testList.remove(2);
        service.removeOrder(date,"2");
        testList.remove(1);
        service.removeOrder(date,"1");
        testList.remove(0);

        assertEquals(0,testList.size(), "List is empty");
    }


    @Test
    public void testEditOrder() throws Exception{

        service.loadProductAndTaxInfo();
        List<Product> productList = service.listAllProducts();

        String orderNumber = "1";
        String customerName = "testEditOrder";

        Product product = productList.get(2);
        StateTax stateInfo = new StateTax("CA",new BigDecimal("25.00").setScale(2, RoundingMode.FLOOR));
        Order order =  new Order(Integer.parseInt(orderNumber),"testGetOrder",stateInfo, product);
        order.setCustomerName("TESTEditORDER");

        String date = "02/02/2023";
        String name = order.getCustomerName();
        String state = order.getState();
        String type = order.getProductType();
        BigDecimal area = order.getArea();

        service.writeOrderToFile(date,order);
        service.listAllOrders("02022023");

        Order retrievedOrder = service.getOrder(orderNumber);
        assertNotNull(retrievedOrder,"Object not null");
        assertEquals(order.getOrderNumber(),retrievedOrder.getOrderNumber(), "Checking order number");
        assertEquals(order.getCustomerName(),retrievedOrder.getCustomerName(), "Checking customer name");
        assertEquals(order.getProductType(), retrievedOrder.getProductType(), "Checking product type");
        assertEquals(order.getArea(), retrievedOrder.getArea(), "Checking area");
        assertEquals(order.getState(), retrievedOrder.getState(), "Checking state");
        assertEquals(order.getTaxRate(), retrievedOrder.getTaxRate(), "Checking tax rate");
        assertEquals(order.getTotal(), retrievedOrder.getTotal(), "Checking total");

        service.listAllOrders("02022023");
        String dateEdit = "02/02/2023";
        String nameEdit = "editName";
        String stateEdit = "WA";
        String typeEdit = "Wood";
        String areaEdit = "933";
        String[] updates = new String[4];
        updates[0] = nameEdit;
        updates[1] = stateEdit;
        updates[2] = typeEdit;
        updates[3] = areaEdit;
        Order updatedOrder = service.editOrder(dateEdit,orderNumber,updates);
        assertEquals(updatedOrder.getOrderNumber(), retrievedOrder.getOrderNumber(), "Checking for matching order numbers");
        assertNotEquals(updatedOrder.getCustomerName(),retrievedOrder.getCustomerName(),"Checking for not matching names");
        assertNotEquals(updatedOrder.getState(),retrievedOrder.getState(),"Checking for not matching states");
        assertNotEquals(updatedOrder.getProductType(), retrievedOrder.getProductType(), "Checking for not matching product types");
        assertNotEquals(updatedOrder.getArea(), retrievedOrder.getArea());

        service.removeOrder(date,orderNumber);
    }

    @Test
    public void testRemoveOrder() throws Exception{

        service.loadProductAndTaxInfo();
        List<Product> productList = service.listAllProducts();
        String orderNumber = "1";
        String customerName = "testremove";

        Product product = productList.get(3);
        StateTax stateInfo = new StateTax("CA",new BigDecimal("25.00").setScale(2, RoundingMode.FLOOR));
        Order order1 =  new Order(Integer.parseInt(orderNumber),"testGetOrder",stateInfo, product);
        order1.setCustomerName("TESTREMOVE-1");

        String date = "02/02/2023";
        String name = order1.getCustomerName();
        String state = order1.getState();
        String type = order1.getProductType();
        BigDecimal area = order1.getArea();

        String orderNumber2 = "2";
        String customerName2 = "testremove2";

        Product product2 = productList.get(1);
        StateTax stateInfo2 = new StateTax("WA",new BigDecimal("9.25").setScale(2, RoundingMode.FLOOR));
        Order order2 =  new Order(Integer.parseInt(orderNumber2),"testGetOrder",stateInfo2, product2);
        order2.setCustomerName("TESTREMOVE-2");

        String name2 = order2.getCustomerName();
        String state2 = order2.getState();
        String type2 = order2.getProductType();
        BigDecimal area2 = order2.getArea();

        String orderNumber3 = "3";
        String customerName3 = "testremove3";

        Product product3 = productList.get(3);
        StateTax stateInfo3 = new StateTax("WA",new BigDecimal("9.25").setScale(2, RoundingMode.FLOOR));
        Order order3 =  new Order(Integer.parseInt(orderNumber3),"testGetOrder",stateInfo3, product3);
        order3.setCustomerName("TESTREMOVE-3");

        String name3 = order3.getCustomerName();
        String state3 = order3.getState();
        String type3 = order3.getProductType();
        BigDecimal area3 = order3.getArea();

        //add the Orders to the file
        service.writeOrderToFile(date,order1);
        service.writeOrderToFile(date,order2);
        service.writeOrderToFile(date,order3);

        //load and capture all the orders under specified date
        List<Order> testList = service.listAllOrders("02022023");

        Order retrievedOrder1 = service.getOrder("1");
        Order retrievedOrder2 = service.getOrder("2");
        Order retrievedOrder3 = service.getOrder("3");

        assertNotNull(testList, "list of orders is not null");
        assertEquals(3, testList.size(), "List of orders should be 3");
        assertTrue(testList.contains(retrievedOrder1), "list contains retrievedOrder1");
        assertTrue(testList.contains(retrievedOrder2), "list contains retrievedOrder2");
        assertTrue(testList.contains(retrievedOrder3), "list contains retrievedOrder3");

        service.removeOrder(date,"2");
        testList.remove(1);

        List<Order> testListRemoved = service.listAllOrders("02022023");
        assertFalse(testList.contains(retrievedOrder2), "list does NOT contains retrievedOrder2");
        assertEquals(testList.size(),2);

        service.removeOrder(date,"3");
        service.removeOrder(date,"1");


    }

}
