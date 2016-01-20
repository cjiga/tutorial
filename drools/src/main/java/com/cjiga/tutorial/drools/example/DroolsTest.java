package com.cjiga.tutorial.drools.example;

import java.util.Arrays;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.kie.api.KieServices;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.cjiga.tutorial.drools.dto.Customer;
import com.cjiga.tutorial.drools.dto.Order;
import com.cjiga.tutorial.drools.dto.Product;

public class DroolsTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*Drools 6.**/
		final KieSession ksession=readKnowledgeBaseKie("OrderKS");
		
		/*Drools 5.**/
//		final KnowledgeBase kbase=readKnowledgeBase();
//		final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

		final List<Order> orders = Arrays.asList(getOrderWithDefaultCustomer(), getOrderWithSilverCustomer(),
				getOrderWithGoldCustomer(), getOrderWithGoldCustomerAndTenProducts());
		for (Order order : orders) {
			ksession.insert(order);
		}
		//Dispara la regla
		ksession.fireAllRules();
		ksession.dispose();
		showResults(orders);
	}
	
	private static KnowledgeBase readKnowledgeBase() {
		final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("rulesOrder.drl"), ResourceType.DRL);
		if (kbuilder.hasErrors()) {
			for (KnowledgeBuilderError error : kbuilder.getErrors()) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Imposible crear knowledge con Order.drl");
		}
		final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}
	
	private static KieSession readKnowledgeBaseKie(String module) {
		final KieServices ks=KieServices.Factory.get();
		final KieContainer kc=ks.getKieClasspathContainer();
		final KieSession ksession=kc.newKieSession(module);
		
		ksession.addEventListener(new DebugAgendaEventListener());
		ksession.addEventListener(new DebugRuleRuntimeEventListener());
		
		return ksession;
	}
	
	private static Order getOrderWithDefaultCustomer() {
		final Order order = new Order(getDefaultCustomer());
		order.addProduct(getProduct1());
		return order;
	}

	private static Order getOrderWithSilverCustomer() {
		final Order order = new Order(getSilverCustomer());
		order.addProduct(getProduct1());
		return order;
	}

	private static Order getOrderWithGoldCustomer() {
		final Order order = new Order(getGoldCustomer());
		order.addProduct(getProduct1());
		return order;
	}

	private static Order getOrderWithGoldCustomerAndTenProducts() {
		final Order order = new Order(getSilverCustomer());
		for (int i = 0; i < 10; i++) {
			order.addProduct(getProduct1());
		}
		return order;
	}
	
	private static Customer getDefaultCustomer() {
		return new Customer(Customer.DEFAULT_CUSTOMER, "Cliente estandar");
	}

	private static Customer getSilverCustomer() {
		return new Customer(Customer.SILVER_CUSTOMER, "Cliente SILVER");
	}

	private static Customer getGoldCustomer() {
		return new Customer(Customer.GOLD_CUSTOMER, "Cliente GOLD");
	}

	private static Product getProduct1() {
		return new Product(1, "Producto 1", 100d);
	}
	
	private static void showResults(List<Order> orders) {
		for (Order order : orders) {
			System.out.println("Cliente " + order.getCustomer().getName() + " productos: " + order.getProducts().size()
					+ " Precio total: " + order.getTotalPrice());
		}
	}
}
