package com.passbolt.server;

import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Server {

	BufferedReader in;
	PrintWriter out;
	String url;
	WebDriver driver;
	String userName;
	String password;

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
		listen();
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
		System.out.println("Listening from here");
		
		//driver = new FirefoxDriver();
		while ((inputLine = in.readLine()) != null) {
			System.out.println(inputLine);
			args = inputLine.split(",");
			setUserName(args[1]);
			setPassword(args[2]);
			
			if (args[0].equalsIgnoreCase("fb")) {
				driver = new FirefoxDriver();
//				driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
//		        driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				launchFacebook();
			} else if (args[0].equalsIgnoreCase("ln")) {
				driver = new FirefoxDriver();
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				launchLinkedIn();
			}
//			 else if (inputLine.equalsIgnoreCase("tw")) {
//			 driver = new FirefoxDriver();
//			 launchTwitter();
//			 }
			else {
				out.write("BAD\n"); // something went wrong
			}
			out.flush();
		}

		System.out.println("Exiting");
		return;
	}

	private void launchLinkedIn() {
		// TODO Auto-generated method stub
		System.out.println("Launching Linkedin");
		driver.get("https://www.linkedin.com/");

		WebElement userQuery = driver.findElement(By.name("session_key"));
		WebElement passQuery = driver.findElement(By.name("session_password"));

		userQuery.sendKeys(getUsername());
		passQuery.sendKeys(getPassword());

		WebElement login = driver.findElement(By.name("submit"));
		login.click();

	}

	public void launchFacebook() {
		System.out.println("Launching fb");
		driver.get("http://www.facebook.com");
		WebElement userQuery = driver.findElement(By.name("email"));
		WebElement passQuery = driver.findElement(By.name("pass"));

		userQuery.sendKeys(getUsername());
		passQuery.sendKeys(getPassword());
		WebElement login;
		if (driver.findElements(By.id("u_0_x")).size()!=0)
			login = driver.findElement(By.id("u_0_x"));
		else
			login = driver.findElement(By.id("u_0_n"));
		
		login.click();
	}

	public void launchTwitter() {
		System.out.println("Launching twitter");
		driver.get("http://www.twitter.com");
		WebElement userQuery = driver.findElement(By.className("text-input email-input"));
		WebElement passQuery = driver.findElement(By.className("text-input"));

		userQuery.sendKeys(getUsername());
		passQuery.sendKeys(getPassword());

		WebElement login = driver.findElement(By.className("submit btn primary-btn js-submit")); 
		login.click();
	}

	private String getUsername() {
		return userName;
	}

	private void setUserName(String arg){
		this.userName = arg;
	}
	private String getPassword() {
		return password;
	}
	private void setPassword(String arg) {
		this.password = arg;
	}

	/**
	 * ` Create a new server that listens on port 5555.
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
