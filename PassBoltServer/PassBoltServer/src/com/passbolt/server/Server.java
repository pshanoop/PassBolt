package com.passbolt.server;

import java.awt.AWTException;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class Server {

	BufferedReader in;
	PrintWriter out;
	String url;

	/**
	 * Start the server on a given port. The server will block until one client
	 * connects. To function nicely, there should only be one client so the
	 * server stops accepting new connections. The server then waits for
	 * commands to be received and handles them.
	 * 
	 * @param port
	 *            The port to listen on
	 * @throws IOException
	 * @throws AWTException
	 * @throws InterruptedException
	 */
	Server(int port) throws IOException, AWTException, InterruptedException {
		System.out.println("Starting Server");
		ServerSocket socket = new ServerSocket(port);
		Socket client = socket.accept(); // accept one client
		System.out.println("Connected");
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new PrintWriter(client.getOutputStream(), true);

		listen(); // listen until the client exits. Then exit gracefully as
					// well.
		System.out.println("Closing");
		in.close();
		out.close();
		socket.close();
		client.close();
	}

	/**
	 * The server will listen for new commands and then handle them using the
	 * ServerLib helper methods.
	 * 
	 * @throws IOException
	 * @throws AWTException
	 * @throws InterruptedException
	 */
	public void listen() throws IOException, AWTException, InterruptedException {
		String inputLine;
		String[] args;
		System.out.println("Listening");
		launchFacebook();
		//launchGoogle();
//		try {
//			launchGoogleLogin();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		fillInDetails();

		while ((inputLine = in.readLine()) != null) { // block until a command
														// is received

			System.out.println(inputLine);
			if (inputLine.equals("exit")) { // The client is shutting down so
											// the server should also exit
				out.write("OK\n");
				break;
			} else if (inputLine.contains("mm")) { // Move mouse - mm:X:Y
				args = inputLine.split(":");
				// args[1] will be the X amount to move, args[2] will be the Y
				// amount to move.
				ServerLib.moveMouseBy(Integer.parseInt(args[1]),
						Integer.parseInt(args[2]));
				System.out.println("move to: " + args[1] + " " + args[2]);
				out.write("OK\n");
			} else if (inputLine.contains("lc")) { // left click
				//ServerLib.click(1); // 1 is left click
				launchFacebook();
				fillInDetails();
				System.out.println("Left Click");
				out.write("OK\n");
			} else if (inputLine.contains("rc")) { // right click
				ServerLib.click(2); // 2 is right click
				System.out.println("Right Click");
				out.write("OK\n");
			} else if (inputLine.contains("sd")) { // scroll down
				ServerLib.scroll(1); // scroll down by 1 "wheel click"
				System.out.println("Scroll Down");
				out.write("OK\n");
			} else if (inputLine.contains("su")) { // scroll up
				ServerLib.scroll(-1); // scroll up by 1 "wheel click"
				System.out.println("Scroll Up");
				out.write("OK\n");
			} else if (inputLine.contains("wr")) { // type a string
				String text = in.readLine(); // The string is on the next line
												// of the buffer
				ServerLib.typeString(text);
				System.out.println("Type: " + text);
				out.write("OK\n");
			} else if (inputLine.contains("fb")) {
			}

			else {
				out.write("BAD\n"); // something went wrong
			}
			out.flush();
		}

		System.out.println("Exiting");
		return;
	}

	private void launchGoogle() {
		// TODO Auto-generated method stub

	    url = "https://www.google.com/accounts/ServiceLogin?service=mail&amp;passive=true&amp;rm=false&amp;continue=http%3A%2F%2Fmail.google.com%2Fmail%2F%3Fui%3Dhtml%26zy%3Dl&amp;bsv=zpwhtygjntrz&amp;scc=1&amp;ltmpl=default&amp;ltmplcache=2&amp;hl=en";
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(new URI(url));
			} catch (IOException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec("xdg-open " + url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	
		
	}

	private void launchGoogleLogin() throws Exception, MalformedURLException, IOException {
		// TODO Auto-generated method stub
		final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38);
		final HtmlPage gmail_login_url = webClient.getPage(url);
		System.out.println("\n GMAIL LOGIN TITLE: " + gmail_login_url.getTitleText());
		HtmlForm gmail_login_form = gmail_login_url.getForms().get(0);
		gmail_login_form.getInputByName("Email").setValueAttribute("meuemail@gmail.com");
		gmail_login_form.getInputByName("Passwd").setValueAttribute("minhasenha");
		HtmlPage post_auth_page = gmail_login_form.getInputByName("signIn").click();
		System.out.println("Done");
		
	}

	private void fillInDetails() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		System.out.println("fillinDetails Added");
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		//final WebClient webClient = new WebClient(BrowserVersion.CHROME);
		final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38);
		// Get the first page
	    final HtmlPage page1 = webClient.getPage(url);

	    System.out.println(page1);
	    // Get the form that we are dealing with and within that form, 
	    // find the submit button and the field that we want to change.
	    System.out.println(page1.getForms());
	    final HtmlForm form = page1.getForms().get(0); //(HtmlForm)page1.getFormByName("login_form");

	    //HtmlTextInput emailField = (HtmlTextInput)page1.getElementById("email");
	    HtmlTextInput emailField = (HtmlTextInput)form.getInputByName("email");
	    HtmlPasswordInput passwordField = (HtmlPasswordInput)form.getInputByName("pass");
	    //HtmlButton button = (HtmlButton) page1.getByXPath("/html/body//form//input[@type='submit' and @value='Log in']");
	    //final HtmlSubmitInput button = (HtmlSubmitInput) form.getInputsByValue("Log in");
	    //HtmlSubmitInput button = form.getInputByName("Log In");
	    //final HtmlTextInput textField = form.getInputByName("pass");

	    // Change the value of the text field
	    System.out.println(emailField);
	    emailField.setValueAttribute("root");
	    passwordField.setValueAttribute("passsssssss");
	    System.out.println("root set:"+emailField);
	    System.out.println(passwordField.getValueAttribute());
	    // Now submit the form by clicking the button and get back the second page.
	    //final HtmlPage page2 = button.click();
	    //System.out.println(page2);
	    //webClient.closeAllWindows();
	}

	public void launchFacebook() {
	    url = "http://www.facebook.com";
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(new URI(url));
			} catch (IOException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec("xdg-open " + url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Create a new server that listens on port 5555.
	 * 
	 * @param args
	 * @throws AWTException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String[] args) throws AWTException,
			InterruptedException, IOException {
		Server s = new Server(5555);
	}

}
