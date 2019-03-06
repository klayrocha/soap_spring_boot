package com.klayrocha.soap.webservices.customersadministration.soap;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.klayrocha.soap.webservices.customersadministration.bean.Customer;
import com.klayrocha.soap.webservices.customersadministration.service.CustomerDetailService;
import com.klayrocha.soap.webservices.customersadministration.soap.exception.CustomerNotFoundException;

import br.com.klayrocha.CustomerDetail;
import br.com.klayrocha.DeleteCustomerRequest;
import br.com.klayrocha.DeleteCustomerResponse;
import br.com.klayrocha.GetAllCustomerDetailRequest;
import br.com.klayrocha.GetAllCustomerDetailResponse;
import br.com.klayrocha.GetCustomerDetailRequest;
import br.com.klayrocha.GetCustomerDetailResponse;
import br.com.klayrocha.InsertCustomerRequest;
import br.com.klayrocha.InsertCustomerResponse;

@Endpoint
public class CustomerDetailEndPoint {
	
	@Autowired
	CustomerDetailService service;
	
	@PayloadRoot(namespace="http://klayrocha.com.br", localPart="GetCustomerDetailRequest")
	@ResponsePayload
	public GetCustomerDetailResponse processCustomerDetailRequest(@RequestPayload GetCustomerDetailRequest req) throws Exception {
		Customer customer = service.findById(req.getId());
		if(customer == null) {
			throw new CustomerNotFoundException("Invalid Customer id "+req.getId());
		}
		return convertToGetCustomerDetailResponse(customer);
	}
	
	private GetCustomerDetailResponse convertToGetCustomerDetailResponse(Customer customer) {
		GetCustomerDetailResponse resp = new GetCustomerDetailResponse();
		resp.setCustomerDetail(convertToCustomerDetail(customer));
		return resp;
	}
	
	private CustomerDetail convertToCustomerDetail(Customer customer) {
		CustomerDetail customerDetail = new CustomerDetail();
		customerDetail.setId(customer.getId());
		customerDetail.setName(customer.getName());
		customerDetail.setPhone(customer.getPhone());
		customerDetail.setEmail(customer.getEmail());
		return customerDetail;
	}
	
	@PayloadRoot(namespace="http://klayrocha.com.br", localPart="GetAllCustomerDetailRequest")
	@ResponsePayload
	public GetAllCustomerDetailResponse processGetAllCustomerDetailRequest(@RequestPayload GetAllCustomerDetailRequest req) {
		List<Customer> customers = service.findAll();
		return convertToGetAllCustomerDetailResponse(customers);
	}
	
	private GetAllCustomerDetailResponse convertToGetAllCustomerDetailResponse(List<Customer> customers) {
		GetAllCustomerDetailResponse resp = new GetAllCustomerDetailResponse();
		customers.stream().forEach(c -> resp.getCustomerDetail().add(convertToCustomerDetail(c)));
		return resp;
	}
	
	@PayloadRoot(namespace="http://klayrocha.com.br", localPart="DeleteCustomerRequest")
	@ResponsePayload
	public DeleteCustomerResponse deleteCustomerRequest(@RequestPayload DeleteCustomerRequest req) {
		DeleteCustomerResponse resp = new DeleteCustomerResponse();
		resp.setStatus(convertStatusSoap(service.deleteById(req.getId())));
		return resp;
	}
	
	private br.com.klayrocha.Status convertStatusSoap(
			com.klayrocha.soap.webservices.customersadministration.helper.Status statusService) {
		if(statusService == com.klayrocha.soap.webservices.customersadministration.helper.Status.FAILURE) {
			return br.com.klayrocha.Status.FAILURE;
		}
		return br.com.klayrocha.Status.SUCCESS;
	}
	
	@PayloadRoot(namespace="http://klayrocha.com.br", localPart="InsertCustomerRequest")
	@ResponsePayload
	public InsertCustomerResponse insertCustomerRequest(@RequestPayload InsertCustomerRequest req) {
		InsertCustomerResponse resp = new InsertCustomerResponse();
		resp.setStatus(convertStatusSoap(service.insert(convertCustomer(req.getCustomerDetail()))));
		return resp;
	}
	
	private Customer convertCustomer(CustomerDetail customerDetail) {
		return new Customer(customerDetail.getId(),customerDetail.getName(),customerDetail.getPhone(),customerDetail.getEmail());
	}
	
}
