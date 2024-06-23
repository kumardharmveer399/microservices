# microservices
customer micriservice

This is a Spring Boot application that manages customer-related operations with CRUD functionality, JWT-based authentication, and role-based authorization.

Customer Table Schema:-	
id		
first_name	
last_name	
gender	
password		
email		
profile_image_path			
is_active	boolean		true	
created_at	datetime		Current_date_time	
updated_at	datetime	


Features
Create a new customer
List all customers with filters
Update customer details
Delete customer (soft delete)
JWT-based authentication and role-based authorization

Prerequisites
Java 17 or later
Maven
Postman (for API testing)

API Endpoints
    Authentication
      register
      URL: /register
      Method: POST
      
       Login
      URL: /login
      Method: POST
      
       logout
      URL: /logout
      

   Customer Management
      Create Customer
      URL: /api/v1/customers
      Method: POST

      List Customers
      URL: /api/v1/customers
      Method: POST
  
      Failure Response:
      HTTP 401 Unauthorized, HTTP 403 Forbidden: Based on the security setup.
      Internal Server Error (HTTP 500)

      Delete Customer
      URL: /api/v1/customers/{customerId}
      Method: DELETE
      Response Body:
      Success (HTTP 204 No Content)
      Failure:
      HTTP 401 Unauthorized, HTTP 403 Forbidden: Based on the security setup.
      Customer Not Found (HTTP 404)

      Note: Soft Delete: Mark the customer as inactive or deleted without actually removing the data (for record-keeping or potential undelete).

      Role-Based Authentication
        Step 1: Add Role to Customer Model
        Step 2: Update Security Configuration
                    @Configuration
                          @EnableWebSecurity
                          public class SecurityConfig extends WebSecurityConfigurerAdapter {
                          
                              // other configurations...
                          
                              @Override
                              protected void configure(HttpSecurity http) throws Exception {
                                  http.csrf().disable()
                                      .authorizeRequests()
                                      .antMatchers("/api/v1/customers/**").hasRole("ADMIN")
                                      .and()
                                      .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
                              }
                          }

          Step 3: Assign Roles in Customer Service
                      @Service
                              public class CustomerService {
                              
                                  @Autowired
                                    private UserRepository userRepository;
                                
                                    // other methods...
                                
                                    public Customer saveCustomer(Customer customer) {
                                        customer.setRole("USER"); // or "ADMIN"
                                        return userRepository.save(customer);
                                    }
                                }


This README.md provides a comprehensive guide to setting up and using the Customer Service API, including details on the endpoints, request/response formats, JWT authentication, and role-based authorization.

        
