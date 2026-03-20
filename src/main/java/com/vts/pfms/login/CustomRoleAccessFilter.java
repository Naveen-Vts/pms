//package com.vts.pfms.login;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Set;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import com.vts.pfms.admin.service.AdminService;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//@Component
//public class  CustomRoleAccessFilter extends OncePerRequestFilter  {
//
//	@Autowired
//	AdminService service;
//	
//	@Autowired
//	LoginRepository Repository;
//	
//    private static final Set<String> ALLOWED_METHODS = Set.of("GET", "POST", "PUT", "PATCH");
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//			throws ServletException, IOException {
//		
//		/*
//		 * if (!ALLOWED_METHODS.contains(request.getMethod())) {
//		 * System.out.println("Methods not allowed");
//		 * response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
//		 * "Method Not Allowed"); return; }
//		 */
//		
//		String [] url = request.getRequestURI().split("/");
//		String ext = url[url.length-1];
//		List<String>extension= Arrays.asList(".jpg",".png",".css",".js",".woff2");
//		
//		if(url[2].equalsIgnoreCase("wr") ||url[2].equalsIgnoreCase("login") || url[2].contains("refresh-captcha") || extension.stream().filter(e->ext.contains(e)).findAny().isPresent() || url[2].equalsIgnoreCase("welcome") || url[2].equalsIgnoreCase("MainDashBoard.htm")
//				|| url[2].equalsIgnoreCase("HeaderModuleList.htm") || url[2].equalsIgnoreCase("HeaderMenu.htm") || url[2].equalsIgnoreCase("NotificationList.htm") || url[2].equalsIgnoreCase("getAllNoticationId.htm")||url[2].contains("LoginPage")) {
//			filterChain.doFilter(request, response);
//			return;
//		}
//		String LoginType=Repository.findByUsername(request.getUserPrincipal().getName()).getLoginType();
//		String requestUrlSegment = url[2].trim();
//		List<Object[]> getFormId = service.getFormId(requestUrlSegment);
//		
//		System.out.println("url -"+requestUrlSegment);
//		
//		boolean isPresent = false;
//		
//		
//		System.out.println("formId size"+getFormId.isEmpty());
//		if(getFormId!=null && getFormId.size()>0) {
//			String formdetaild = getFormId.get(0)[0].toString();
//			List<Object[]> formUrlsList = service.getFormUrlList(LoginType,formdetaild);
//			isPresent=formUrlsList!=null && formUrlsList.size()>0?true:false;
//		
//			System.out.println("formUrlsList--------->"+formUrlsList.size());
//			
//			System.out.println("isPresent ---->"+isPresent);
//		
//		}
//
//		if(LoginType.equalsIgnoreCase("A")) {
//		 filterChain.doFilter(request, response);
//		 return;
//		}
//		
//		
//		if(getFormId == null || getFormId.isEmpty()) {
//			 filterChain.doFilter(request, response);
//		}
//		
//		if(isPresent) {
//			filterChain.doFilter(request, response);
//		}
//		else{
//			if (!response.isCommitted()) {
//		    response.sendRedirect(request.getContextPath() + "/MainDashBoard.htm?resultfail=Access Denied");
//		    }
//		}
//	}
//}
//
