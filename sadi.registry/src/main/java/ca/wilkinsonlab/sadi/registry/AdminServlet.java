package ca.wilkinsonlab.sadi.registry;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import org.sadiframework.beans.ServiceBean;
import ca.wilkinsonlab.sadi.registry.utils.Twitter;

import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

@SuppressWarnings("serial")
public class AdminServlet extends HttpServlet
{
	private static final Logger log = Logger.getLogger( AdminServlet.class );
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{   System.out.println("EL PATH ES=1");
		if (request.isUserInRole("admin")) {
			AccessToken accessToken = null;
			try {
				accessToken = Twitter.retrieveAccessToken();
			} catch (Exception e) {
				log.error("error retrieving access token", e);
			}
			if (accessToken == null) {
				HttpSession session = request.getSession(true);
				RequestToken requestToken = (RequestToken)session.getAttribute("requestToken");
				if (requestToken == null) {
					try {
						requestToken = Twitter.getRequestToken();
						session.setAttribute("requestToken", requestToken);
					} catch (Exception e) {
						request.setAttribute("error", String.format("error generating request token: %s", e.toString()));
						log.error("error generating request token", e);
					}
				}
				String pin = request.getParameter("pin");
				if (pin != null) {
					accessToken = Twitter.getAccessToken(requestToken, pin);
					try {
						Twitter.storeAccessToken(accessToken);
					} catch (Exception e) {
						request.setAttribute("error", String.format("error generating request token: %s", e.toString()));
						log.error("error storing request token", e);
					}
				} else {
					request.setAttribute("authorizationURL", requestToken.getAuthorizationURL());
				}
			}
			request.setAttribute("accessToken", accessToken); // might be null...
			getServletConfig().getServletContext().getRequestDispatcher("/admin/index.jsp").forward(request, response);
		} else if (request.isUserInRole("unregister")) {
			String serviceURI = request.getParameter("serviceURI");
			if (serviceURI != null) {
				ServiceBean service = new ServiceBean();
				service.setURI(serviceURI);
				request.setAttribute("service", service);
				
				Registry registry = null;
				try {
					registry = Registry.getRegistry();
					registry.unregisterService(serviceURI);
				} catch (Exception e) {
					log.error(String.format("unregistration failed for %s: %s", serviceURI, e.getMessage()), e);
					request.setAttribute("error", e.getMessage() != null ? e.getMessage() : e.toString());
				} finally {
					if (registry != null)
						registry.getModel().close();
				}
			}
			getServletConfig().getServletContext().getRequestDispatcher("/admin/unregister.jsp").forward(request, response);
		} else {
			log.error("attempted unauthorized access to admin servlet");
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		System.out.println("EL PATH ES=3");
		doGet(request, response);
	}
}
