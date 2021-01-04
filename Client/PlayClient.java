package nep.Client;


/*
 * CONTENT
 * **************************************************
 * 	1) Socket 	    		      		   (Line 33)
 *  2) Data Input & Output    			   (Line 46)
 *  3) Changing Default Name 		       (Line 59)
 *  4) Game Start     					   (Line 65)
 *  5) Game Session and Connection		   (Line 86)
 *  6) Dice Roll and Player's Turn         (Line 107)
 *  7) Card Shuffle and Selection	       (Line 139)
 *  8) Continue Process and Card Selection (Line 176)
 *  9) Game Result, Restart and Termination(Line 233)
 *  10)Exception Handler  				   (Line 302)
 ****************************************************
 */


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class PlayClient {
	public static void main(String[] args) {

		try {

			//====================================1) Socket =============================================

			Socket socket = new Socket ("localhost",9001);
			boolean connect = socket.isConnected();
			System.out.println("Connected to Server: "+connect);

			if (connect==true) {
				System.out.println("\n---------- Connected to the Server ----------");
			} else {
				System.out.println("\n---------- Not Connected to the Server ------------\nExceed Server Limit!");
			}


			//===================================2) Data Input & Output =============================================

			DataInputStream fromServer = new DataInputStream (socket.getInputStream());
			DataOutputStream toServer = new DataOutputStream (socket.getOutputStream());
			Scanner input = new Scanner (System.in);

			new Thread (new Runnable() {

				@Override
				public void run() {

					try {

						//===================================3) Changing Default Name =============================================

						System.out.print("\nPlease Enter Your Username: ");
						String userName = input.next();
						toServer.writeUTF(userName);

						//===================================4) Game Start =========================================================		

						System.out.println("\n===============================================");
						System.out.println("\tWelcome to the Twenty-One Game!");
						System.out.println("===============================================");

						System.out.print("\nConnecting With Other Player ");
						System.out.print(".");
						Thread.sleep(30);
						System.out.print(".");
						Thread.sleep(30);
						System.out.print(".");
						Thread.sleep(30);
						System.out.print(".");
						Thread.sleep(30);
						System.out.print(".");
						Thread.sleep(30);

						String play1 = null;
						String play2 = null;

						//===================================5) Game Session and Connection =============================================

						while (true) {
							int session = fromServer.readInt();
							System.out.println("\n\nConnected to game session: "+session);

							System.out.print("\n--------Please Wait For Other Player--------");

							int size = fromServer.readInt();
							int k = 1;
							System.out.println("\n");
							

							for (int i=0; i<size;i++) {
								String name = fromServer.readUTF();
								System.out.println("\tPlayer "+k+" ["+name+"] has Connected");
								Thread.sleep(500);
								k++;
							}

							System.out.println("\n===============================================");

							//===================================6) Dice Roll and Player's Turn =============================================

							boolean playerExit = false;
							while (true) {

								System.out.println("\n"+fromServer.readUTF());
								boolean win = false;

								while (true) {

									while (win==false) {

										System.out.println("\n"+fromServer.readUTF());
										Thread.sleep(30);
										System.out.println("Result: "+fromServer.readInt());
										System.out.println("\n\t"+fromServer.readUTF());

										String compareResult = fromServer.readUTF();

										if (compareResult.equalsIgnoreCase("draw")) {
											win = false;
										} else {
											win = true;
										}

										Thread.sleep(500);

									}

									System.out.println(fromServer.readUTF());
									System.out.println("\n"+fromServer.readUTF());

									//===================================7) Continue Process and Card Selection =============================================

									String decision;
									do {

										System.out.print(fromServer.readUTF());
										decision = input.next();
										toServer.writeUTF(decision);

									} while (!decision.equalsIgnoreCase("y") && !decision.equalsIgnoreCase("n"));

									playerExit = fromServer.readBoolean();


									if (decision.equalsIgnoreCase("n")) {

										System.out.println("\nQuiting Game, \nConnection Terminated!");
										System.out.println("-------------");
										playerExit=true;
										break;

									} else {

										if (playerExit==true) {
											System.out.println("\nOpponent Has Left The Game! \n\nNo Opponent Found!, Quitting Game!");
											System.out.println("-----------------------------------");
											break;
										}

										System.out.println("\n  Card Left");
										System.out.println("**************");
										System.out.println("*\t     *");
										System.out.println("*     "+fromServer.readInt()+"     *");
										System.out.println("*\t     *");
										System.out.println("**************");
										System.out.println(fromServer.readUTF());

										//===================================8) Card Taking and Stop =============================================

										int take = 0;
										boolean stop = false;
										while (stop==false || take<=3) {

											int playerSize = fromServer.readInt();
											int deckSize = fromServer.readInt();
											take++;

											if (take==4) {
												stop = true;
											}

											System.out.println("\n  Card Left");
											System.out.println("**************");
											System.out.println("*\t     *");
											System.out.println("*     "+deckSize+"     *");
											System.out.println("*\t     *");
											System.out.println("**************");

											System.out.print("\n>> Your Cards: ");
											for (int i=0; i<playerSize;i++) {

												int twoCard = fromServer.readInt();
												System.out.print(""+twoCard+" | ");

											}

											String condition;
											String inputDecision;

											System.out.println("\n>> Total: "+fromServer.readInt());
											play1 = fromServer.readUTF();
											play2 = fromServer.readUTF();

											do {
												System.out.println("\n"+fromServer.readUTF());
												System.out.println("-----------------------------------------------");



												System.out.println("\n"+fromServer.readUTF());
												System.out.print(""+fromServer.readUTF());
												inputDecision = input.next();
												toServer.writeUTF(inputDecision);
												condition = fromServer.readUTF();

											} while (condition.equalsIgnoreCase("loop"));

											if (inputDecision.equalsIgnoreCase("s")) {
												take = 4;
												stop = true;
											}

										}

										//===================================9) Game Result, Restart and Termination =============================================

										while (!play1.equalsIgnoreCase("true") || !play2.equalsIgnoreCase("true")) {

											System.out.println("\n-------Waiting For Result----------");
											play1 = "true";
											play2 = "true";
											break;

										}

										playerExit = fromServer.readBoolean();

										if (playerExit==true) {
											System.out.println("\nOpponent Has Left The Game \nNo Opponent Found!,Quiting The Game!");
											break;
										}


										String allDone = null;
										if (play1.equalsIgnoreCase("true") && play2.equalsIgnoreCase("true")) {
											System.out.println("\nResult:");
											System.out.println("---------");
											System.out.println("\n"+fromServer.readUTF());
											System.out.println(fromServer.readUTF());	
											System.out.println("\n"+fromServer.readUTF());

											allDone = fromServer.readUTF();
										}

										String userCon = null;
										do {
											if (allDone.equalsIgnoreCase("continue")){
												System.out.println("\n-----------------------------------");
												System.out.print("Do You Want to Try Again (y/n) ?: ");
												userCon = input.next();
												System.out.println("\n-----------------------------------");
												toServer.writeUTF(userCon);
											}

										} while (!userCon.equalsIgnoreCase("y") && !userCon.equalsIgnoreCase("n"));

										System.out.print(fromServer.readUTF());
										break;


									}
								}

								if (playerExit==true) {
									break;
								}


								String userInput = input.next();
								toServer.writeUTF(userInput);

								if (fromServer.readBoolean()==true) {
									System.out.println(fromServer.readUTF());
									break;
								}


							}

							break;

						}

						//===================================10) Exception Handler =============================================


					} catch (IOException | InterruptedException e) {

						e.printStackTrace();
					} 


				}

			}).start();

			

		} catch (IOException e) {
			System.out.println("\n---------- Not Connected to the Server ------------");
			System.out.println("\n[INFO] **Error occured because of connection refused by the server**\n\n[INFO] Please check your address and port\n\n[INFO] or Player Limit Exceed on the Server!");


		}


	}
}
