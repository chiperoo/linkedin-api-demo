package org.apiDemo;

import java.util.Scanner;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class ApiDemo {
	
	private final static String API_KEY = "1zux6h5lt401";
	private final static String API_SECRET = "r49i7fAwuBOoQ80A";
	private final static String PROFILE_URL = "http://api.linkedin.com/v1/people/~";
	private final static String INVITATION_URL = "http://api.linkedin.com/v1/people/~/mailbox";
	private final static String LIKED_URL_BEGIN = "http://api.linkedin.com/v1/people/~/network/updates/key=";
	private final static String LIKED_URL_END = "/is-liked";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		Scanner in = new Scanner(System.in);
		// This follows the quickstart guide located at
		// https://github.com/fernandezpablo85/scribe-java/wiki/Getting-Started
		//
		// as well as the example at
		// https://github.com/fernandezpablo85/scribe-java/blob/master/src/test/java/org/scribe/examples/LinkedInExample.java
		
		// Quickstart Guide Step 1
		OAuthService service = new ServiceBuilder()
			.provider(LinkedInApi.class)
			.apiKey(API_KEY)
			.apiSecret(API_SECRET)
			//.debug()
			.build();
		
		// Quickstart Guide Step 2
		Token requestToken = service.getRequestToken();
		
		// Quickstart Guide Step 3
		String authUrl = service.getAuthorizationUrl(requestToken);
		
	    System.out.println("Now go and authorize here:");
	    System.out.println(authUrl);
	    System.out.println("And paste the verifier here");
	    System.out.print(">>");
		
		// Quickstart Guide Step 4 
		Verifier v = new Verifier(in.nextLine());
		Token accessToken = service.getAccessToken(requestToken, v);
		
		// Quickstart Step 5
		OAuthRequest request2 = new OAuthRequest(Verb.GET, PROFILE_URL);
		service.signRequest(accessToken, request2);
		Response response2 = request2.send();
		System.out.println(response2.getBody());
		
// Sample XML document to generate an Invitation request
//		<?xml version='1.0' encoding='UTF-8'?>
//		<mailbox-item>
//		  <recipients>
//		    <recipient>
//		      <person path="/people/email=a_user@domain.com">
//		        <first-name>Richard</first-name>
//		        <last-name>Brautigan</last-name>
//		      </person>
//		    </recipient>
//		  </recipients>
//		  <subject>Invitation to Connect</subject>
//		  <body>Please join my professional network on LinkedIn.</body>
//		  <item-content>
//		    <invitation-request>
//		      <connect-type>friend</connect-type>
//		    </invitation-request>
//		  </item-content>
//		</mailbox-item>
		
		// Invite myself on a different email address
		OAuthRequest request3 = new OAuthRequest(Verb.POST, INVITATION_URL);
		request3.addHeader("Content-Type", "text/xml");
		
		// make an XML document using dom4j
		Document doc = DocumentHelper.createDocument();
		Element mailboxItem = doc.addElement("mailbox-item");
		Element recipients = mailboxItem.addElement("recipients");
		Element recipient = recipients.addElement("recipient");
		Element person = recipient.addElement("person").addAttribute("path", "/people/email=chiperoo101@gmail.com");
		person.addElement("first-name").addText("John");
		person.addElement("last-name").addText("Doe");
		mailboxItem.addElement("subject").addText("Invitation to Connect");
		mailboxItem.addElement("body").addText("Please join my professional network on LinkedIn.");
		Element itemContent = mailboxItem.addElement("item-content");
		Element invitationRequest = itemContent.addElement("invitation-request");
		invitationRequest.addElement("connect-type").addText("friend");
		
		request3.addPayload(doc.asXML());
		service.signRequest(accessToken, request3);
		Response response3 = request3.send();
		// no body, just header
		System.out.println(response3.getBody());
		System.out.println(response3.getHeaders().toString());
	
		// Like an update (PUT call)
		// Network update key for article:
		// UNIU-10402481-5767475231761383424-SHARE
		// url: http://www.linkedin.com/today/post/article/20130723160110-658789-7-qualities-of-a-truly-loyal-employee
		String updateKey = "UNIU-10402481-5767475231761383424-SHARE";
		OAuthRequest request4 = new OAuthRequest(Verb.PUT, LIKED_URL_BEGIN + updateKey + LIKED_URL_END);
		request4.addHeader("Content-Type", "text/xml");
		
		// make an XML document using dom4j
		Document doc2 = DocumentHelper.createDocument();
		doc2.addElement("is-liked").addText("false");
		
		request4.addPayload(doc2.asXML());
		service.signRequest(accessToken, request4);
		Response response4 = request4.send();
		// no body, just header
		System.out.println(response4.getBody());
		System.out.println(response4.getHeaders().toString());
	}

}
