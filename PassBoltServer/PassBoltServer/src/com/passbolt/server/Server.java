package com.passbolt.server;

import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Server {

	BufferedReader in;
	PrintWriter out;
	String url;
	WebDriver driver;

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
		System.out.println("Listening");
		driver = new FirefoxDriver();
		while ((inputLine = in.readLine()) != null) {
			System.out.println(inputLine);
			if (inputLine.equals("exit")) {
				out.write("OK\n");
				break;
			} else if (inputLine.equalsIgnoreCase("fb")) {
				launchFacebook();

			} else if (inputLine.equalsIgnoreCase("ln")) {
				launchLinkedIn();
			}
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

		WebElement login = driver.findElement(By.id("u_0_x"));
		login.click();
	}

	private String getUsername(){
		return "***************";
	}
	private String getPassword(){
		return "**********";
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
