package nep.Server;

/*
 * CONTENT
 * **************************************************
 * 	1) ServerSocket 	    		       (Line 37)
 *  2) Accept Player    			       (Line 52)
 *  3) Data Input & Output  		       (Line 83)
 *  4) Changing Default Player's Name      (Line 97)
 *  5) Game Start					       (Line 147)
 *  6) Dice Roll to Indicate Turn          (Line 157)
 *  7) Card Shuffle and Selection	       (Line 207)
 *  	7.1) Player 1				       (Line 265)
 *  	7.2) Player 2				       (Line 375)
 *  8) Game Restart and Terminate Process  (Line 491)
 *  9) Automated Result					   (Line 523)
 *  10) Loops and end Termination Process  (Line 630)
 ****************************************************
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class PlayServer {
	
	public static void main(String[] args) {

		try {
			
			//===================================1) ServerSocket =============================================
			
			ServerSocket serverSocket = new ServerSocket (9001);
			System.out.println("Server is running");
			System.out.println("=================");
			System.out.println("\n---------- Waiting For Connection! ----------");
			int sessions = 0;
			int clientNumber = 1;
			ArrayList <Integer> list = new ArrayList <>();
			ArrayList <Integer> userInput = new ArrayList <>();
			Scanner input = new Scanner (System.in);
			userInput.add(clientNumber);

			while (userInput.get(0) < 2) { //while1
				
			//===================================2) Accept Player =============================================

				if (userInput.get(0)==2) { //JIC
					break;
				}

				int gSession=sessions+1;
				list.add(gSession);
				list.set(0,gSession);

				Socket client1 = serverSocket.accept();
				System.out.println("\nGame Session "+list.get(0));
				InetAddress inetAddress1 = client1.getInetAddress();
				System.out.println(list.get(0) + ": "+ "Player 1 has connected to the server ["+inetAddress1+ "]");

				clientNumber++;
				userInput.set(0, clientNumber);
				Socket client2 = serverSocket.accept();
				InetAddress inetAddress2 = client2.getInetAddress();
				System.out.println(list.get(0) + ": "+ "Player 2 has connected to the server ["+inetAddress2+"]");


				boolean playerExit1;

				new Thread (new Runnable() {

					@Override
					public void run() {

						try {

			 //===================================3) Data Input & Output Stream =============================================
							
							DataInputStream fromClient1 = 
									new DataInputStream (client1.getInputStream());
							DataOutputStream toClient1 = 
									new DataOutputStream (client1.getOutputStream());
							DataInputStream fromClient2 = 
									new DataInputStream (client2.getInputStream());
							DataOutputStream toClient2 = 
									new DataOutputStream (client2.getOutputStream());


							while (true) {//while2

			//===================================4) Changing Default Player's Name =============================================
								
								String ClientName = null;
								int k = 0;
								ArrayList <String> changeName = new ArrayList <>();
								String CompareName1 = null;

								if (fromClient1.available()>=0) {
									
									k=1;
									ClientName = fromClient1.readUTF();
									changeName.add(ClientName);
									System.out.println("\n[session: "+list.get(0)+"] Player "+k+" has change the default username to : "+ClientName);
									toClient1.writeInt(list.get(0));
									CompareName1 = ClientName;

								}

								if (fromClient2.available()>=0) {
									String CompareName = fromClient2.readUTF();
									
									if (CompareName.equalsIgnoreCase(CompareName1)) {
										ClientName = CompareName+"(2)";
										System.out.println("[WARN] duplicated Name detected, change to default convention! ");
										
									} else {
										
										ClientName = CompareName;
										
									}

									k =2;
									changeName.add(ClientName);
									System.out.println("[session: "+list.get(0)+"] Player "+k+" has change the default username to : "+ClientName);
									toClient2.writeInt(list.get(0));

								}

								toClient1.writeInt(changeName.size());
								toClient2.writeInt(changeName.size());

								for (int i = 0 ; i < changeName.size(); i++) {

									toClient1.writeUTF(changeName.get(i));
									toClient2.writeUTF(changeName.get(i));

								}
								int addSession = 0;
								while (true) {

				 //===================================5) Game Start=============================================
									
									addSession++;
									list.set(0, addSession);
									toClient1.writeUTF("***********Let's Start The Game************");
									toClient2.writeUTF("***********Let's Start The Game************");

									boolean win = false;
									boolean player1 = false;	
									
				//===========================6) Dice Roll to Indicate Turn ==========================================
									
									while (win==false) { //while3

										toClient1.writeUTF("Role Dice To Indicate turn: ");
										toClient2.writeUTF("Role Dice To Indicate turn: ");


										int dice1 = (int)((Math.random()*100)%6)+1;
										int dice2 = (int)((Math.random()*100)%6)+1;

										toClient1.writeInt(dice1);
										toClient2.writeInt(dice2);
										toClient1.writeUTF(dice1+" VS "+dice2);
										toClient2.writeUTF(dice2+" VS "+dice1);

										if (dice1==dice2) {
											
											toClient1.writeUTF("draw");
											toClient2.writeUTF("draw");
											System.out.println("[Session: "+list.get(0)+"] Dice rolled is draw, Roll again.");
											
										} else if (dice1>dice2){
											
											win = true;
											toClient1.writeUTF("win");
											toClient2.writeUTF("win");
											System.out.println("[Session: "+list.get(0)+"] Player 1 win in dice roll!");
											toClient1.writeUTF("\nPlayer 1 [" + changeName.get(0)+ "] has win the dice game. \nPlayer 1 Play First!");
											toClient2.writeUTF("\nPlayer 1 [" + changeName.get(0)+ "] has win the dice game. \nPlayer 1 Play First!");
											toClient1.writeUTF("----------Your Turn!----------");
											toClient2.writeUTF("----------Please Wait for Your Turn!----------\n\nCurrently Playing: "+changeName.get(0));
											player1 = true;
											
										} else {
											
											win = true;
											toClient1.writeUTF("win");
											toClient2.writeUTF("win");
											System.out.println("[Session: "+list.get(0)+"] Player 2 win in dice roll!");
											toClient1.writeUTF("\nPlayer 2 [" + changeName.get(1)+ "] has win the dice game. \nPlayer 2 Play First!");
											toClient2.writeUTF("\nPlayer 2 [" + changeName.get(1)+ "] has win the dice game. \nPlayer 2 Play First!");
											toClient2.writeUTF("----------Your Turn!----------");
											toClient1.writeUTF("----------Please Wait for Your Turn!------------\n\nCurrently Playing: "+changeName.get(1));

										}

									} //while3
									
									
				//===================================7) Card Shuffle and Selection =============================================

									ArrayList <Integer> card = new ArrayList <> ();
									ArrayList <Integer> cardValue = new ArrayList <> ();
									ArrayList <Integer> containDeck = new ArrayList <> ();


									for (int i = 0; i < 52; i++ ) {
										card.add(i);
										containDeck.add(i);

										if (i !=0 && i<=13) {

											cardValue.add(i);
										}
									}

									Collections.shuffle(cardValue);

									int x= 1;
									int i = 0;
									while (x<=4) {

										for (int j = 0; j < cardValue.size();j++) {

											if (cardValue.get(j) > 11) {
												containDeck.set(card.get(i), 10);
											} else {
												containDeck.set(card.get(i), cardValue.get(j));
											}

											i++;
										}
										x++;

									}
									
									
									

									boolean playerExit = false;
									boolean player2 = false;
									ArrayList <Integer> player1List = new ArrayList <> ();
									ArrayList <Integer> player2List = new ArrayList <> ();
									int totalNum = 0;
									int totalNum2 = 0;
									String play1 = null;
									String play2 = null;
									int take = 0;
									int take2 = 0;
									boolean noExit = false;
									boolean noExit2 = false;
									
									
				//===================================7) Players Taking Card Process =============================================

									while (true) { //while 4
										
									//================================7.1) Player 1 ===============================
		
										String inputDecision = null;
										if (player1==true) { //while5				

											if (play2==null) {
												play2 = "false";
											}

											String decision;

											do {
												
												toClient1.writeUTF("\nDo you want to continue the Game? (Y/N): ");
												decision = fromClient1.readUTF();
												play1="true";
												
											} while (!decision.equalsIgnoreCase("y") && !decision.equalsIgnoreCase("n"));

											toClient1.writeBoolean(playerExit);


											if (decision.equalsIgnoreCase("n")) {
												playerExit = true;
												noExit = true;

											} else if (decision.equalsIgnoreCase("y")) {

												if (playerExit==true) {
													break;
												}

												toClient1.writeInt(containDeck.size());
												toClient1.writeUTF("\n-- Giving out 2 Cards From deck --");



												for (int l = 0; l<2 ; l++) {
													int index = (int)(Math.random()*100)%52;
													int numCard = containDeck.get(index);
													player1List.add(numCard);
													containDeck.remove(index);

												}
												
												take = 0;
												boolean stop = false;
												
												while (stop==false || take<=3) { //while6
													
													toClient1.writeInt(player1List.size());
													toClient1.writeInt(containDeck.size());
													take++;
													
													if (take==4) {
														stop = true;
													}
													
													totalNum = 0;
													for (int d = 0; d<player1List.size();d++) {
														toClient1.writeInt(player1List.get(d));
														totalNum += player1List.get(d);

													}
													
													toClient1.writeInt(totalNum);
													System.out.println("[Session: "+list.get(0)+"] Player 1 card: "+player1List);
													toClient1.writeUTF(play1);
													toClient1.writeUTF(play2);


													while (true) { // while7
														toClient1.writeUTF("Take one card or Stop and pass to other player.");
														toClient1.writeUTF("Please Select the action below to proceed: ");
														toClient1.writeUTF("Take (t) / Stop (s): ");
														inputDecision = fromClient1.readUTF();

														if (inputDecision.equalsIgnoreCase("t")) {

															int index = (int)(Math.random()*100)%containDeck.size();
															int num = containDeck.get(index);
															player1List.add(num);
															containDeck.remove(index);
															toClient1.writeUTF("notloop");
															break;


														} else if (inputDecision.equalsIgnoreCase("s")) {
															toClient1.writeUTF("notloop");
															stop = true;
															break;
														} else {
															toClient1.writeUTF("loop");
														}

													} //while7



													if (inputDecision.equalsIgnoreCase("s")) {
														take = 4;
														stop=true;
													}

												} //while 6	

											}

										} //while5

										//================================7.2) Player 2 ===============================


										if (player2==false) {

											if (play1==null) {
												play1 = "false";
											}

											String decision;
											do {
												
												toClient2.writeUTF("\nDo you want to continue the Game? (Y/N): ");
												decision = fromClient2.readUTF();
												play2 = "true";
												
											} while (!decision.equalsIgnoreCase("y") && !decision.equalsIgnoreCase("n"));

											toClient2.writeBoolean(playerExit);

											if (decision.equalsIgnoreCase("n")) {
												playerExit = true;
												player2=true;
												noExit2 = true;


											} else if (decision.equalsIgnoreCase("y")) {


												if (playerExit==true) {
													break;
												}

												player2=true;

												toClient2.writeInt(containDeck.size());
												toClient2.writeUTF("\n-- Giving out 2 Cards From deck --");



												for (int l = 0; l<2 ; l++) {
													int index = (int)(Math.random()*100)%containDeck.size();
													int numCard = containDeck.get(index);
													player2List.add(numCard);
													containDeck.remove(index);

												}

												take2 = 0;
												boolean stop = false;
												
												while (stop==false || take2<=3 ) {

													toClient2.writeInt(player2List.size());
													toClient2.writeInt(containDeck.size());
													take2++;
													if (take2==4) {
														stop = true;
													}
													totalNum2 = 0;
													for (int d = 0; d<player2List.size();d++) {
														toClient2.writeInt(player2List.get(d));
														totalNum2 += player2List.get(d);

													}
													
													toClient2.writeInt(totalNum2);
													System.out.println("[Session: "+list.get(0)+"] Player 2 card: "+player2List);
													toClient2.writeUTF(play1);
													toClient2.writeUTF(play2);


													while (true) {
														toClient2.writeUTF("Take one card or Stop and pass to other player.");
														toClient2.writeUTF("Please Select the action below to proceed: ");
														toClient2.writeUTF("Take (t) / Stop (s): ");
														inputDecision = fromClient2.readUTF();

														if (inputDecision.equalsIgnoreCase("t")) {

															int index = (int)(Math.random()*100)%containDeck.size();
															int num = containDeck.get(index);
															player2List.add(num);
															containDeck.remove(index);
															toClient2.writeUTF("notloop");
															break;


														} else if (inputDecision.equalsIgnoreCase("s")) {
															toClient2.writeUTF("notloop");
															stop= true;
															break;

														} else {

															toClient2.writeUTF("loop");
														}

													}



													if (inputDecision.equalsIgnoreCase("s")) {
														take2 = 4;
														stop=true;
													}

												}



											}

										}


					//===================================8) Game Restart and Terminate Process Handler =============================================



										boolean proceed = true;

										if (player1==true && player2==true) {
											
											if (playerExit==true) {
												
												if (noExit==true && noExit2==true) {
													System.out.println("Player 1 and Player 2 Exit the Game!");
													break;
													
												} else {
													toClient1.writeBoolean(proceed);
													toClient2.writeBoolean(proceed);
													System.out.println("Player 1 and Player 2 Exit the Game!");
												}


											} else {
												proceed=false;
												toClient1.writeBoolean(proceed);
												toClient2.writeBoolean(proceed);
											}



										}

										
				            	//===================================9) Automated Result =============================================
										
										String allDone = "wait";
										if (play1.equalsIgnoreCase("true") && play2.equalsIgnoreCase("true")) {



											toClient1.writeUTF("Player 1 Cards: "+totalNum);
											System.out.println("\n>>>> Player 1 Total: "+totalNum+ " |VS| "+"Player 2 Total: "+totalNum2);
											toClient1.writeUTF("Player 2 Cards: "+totalNum2);
											toClient2.writeUTF("Player 1 Cards: "+totalNum);
											toClient2.writeUTF("Player 2 Cards: "+totalNum2);

											if (totalNum == totalNum2 && totalNum<22 && totalNum2 <22) {
												toClient1.writeUTF("The game is draw!");
												toClient2.writeUTF("The game is draw!");
												System.out.println(">>>> The game is draw!");

											} else if (totalNum>totalNum2 && totalNum<22) {

												toClient1.writeUTF("Player 1 win with card total: "+totalNum);
												toClient2.writeUTF("Player 1 win with card total: "+totalNum);
												System.out.println(">>>> Player 1 win with card total: "+totalNum);

											} else if (totalNum2>totalNum && totalNum2<22) {

												toClient1.writeUTF("Player 2 win with: "+totalNum2);
												toClient2.writeUTF("Player 2 win with: "+totalNum2);
												System.out.println(">>>> Player 2 win with: "+totalNum2);

											} else if (totalNum == 21) {

												toClient1.writeUTF("Congratulations, Player 1 win with: "+totalNum);
												toClient2.writeUTF("Congratulations, Player 1 win with: "+totalNum);
												System.out.println(">>>> Congratulations, Player 1 win with: "+totalNum);

											} else if (totalNum2 == 21) {

												toClient1.writeUTF("Congratulations, Player 2 win with: "+totalNum2);
												toClient2.writeUTF("Congratulations, Player 2 win with: "+totalNum2);
												System.out.println(">>>> Congratulations, Player 2 win with: "+totalNum2);

											} else if (totalNum>=22 && totalNum2<22 || totalNum>=22 && totalNum>totalNum2 && totalNum2<22){

												toClient1.writeUTF("Player 2 win with: "+totalNum2 +"\nPlayer 1 lose with card over 21!");
												toClient2.writeUTF("Player 2 win with: "+totalNum2 +"\nPlayer 1 lose with card over 21!");
												System.out.println(">>>> Player 2 win with: "+totalNum2 +", Player 1 lose with card over 21!");

											} else if (totalNum<22 && totalNum2>=22) {

												toClient1.writeUTF("Player 1 win with: "+totalNum +"\nPlayer 2 lose with card over 21!");
												toClient2.writeUTF("Player 1 win with: "+totalNum +"\nPlayer 2 lose with card over 21!");
												System.out.println(">>>> Player 1 win with: "+totalNum +", Player 2 lose with card over 21!");

											} else if (totalNum>=22 && totalNum2>=22) {

												toClient1.writeUTF("The game is draw!, No one get '21'");
												toClient2.writeUTF("The game is draw!, No one get '21'");
												System.out.println(">>>> The game is draw!, No one get '21'");

											} else if (totalNum>=22 && totalNum2<22 && totalNum2<totalNum) {

												toClient1.writeUTF("Player 2 win with: "+totalNum2 +"\nPlayer 1 lose with card over 21!");
												toClient2.writeUTF("Player 2 win with: "+totalNum2 +"\nPlayer 1 lose with card over 21!");
												System.out.println(">>>> Player 2 win with: "+totalNum2 +", Player 1 lose with card over 21!");



											} else if (take2 == 4 && take == 4 && totalNum<22 && totalNum2<22) {
												toClient1.writeUTF("The game is draw!, Both player take 5 cards");
												toClient2.writeUTF("The game is draw!, Both player take 5 cards");
												System.out.println(">>>> The game is draw!, Both player take 5 cards");

											} else {
												toClient1.writeUTF("No condition for this!");
												toClient2.writeUTF("No condition for this!");
												System.out.println(">>>> No condition for this!");
											}


											allDone = "continue";
											toClient1.writeUTF(allDone);
											toClient2.writeUTF(allDone);

											break;
										}

					//===================================Players 1 Turn =============================================

										if (player1 != true) {
											player1 = true;
											play1 = "true";
										}




									} // while 4
									

									if (playerExit==true) {
										break;
									}

									String Again; 
									String Again2;
									
				   //=================================== 10) Loops and Termination Process =============================================

									while (true) {

										Again = fromClient1.readUTF();
										Again2 = fromClient2.readUTF();

										if (Again.equalsIgnoreCase("y") || Again.equalsIgnoreCase("n")) {
											break;
										}


										if (Again2.equalsIgnoreCase("y") || Again2.equalsIgnoreCase("n")) {
											break;
										}

									} 

									boolean dualExit = false;

									if (Again.equalsIgnoreCase("y") && Again2.equalsIgnoreCase("y")){ //1

										toClient1.writeUTF("Waiting for other client to connect \n\nEnter 'Y' To continue: ");
										toClient2.writeUTF("Waiting for other client to connect \n\nEnter 'Y' To Continue: ");
										String enter = fromClient1.readUTF();
										String enter2 = fromClient2.readUTF();
										
										if (enter.equalsIgnoreCase("y") && enter2.equalsIgnoreCase("y")) {

											toClient1.writeBoolean(dualExit);
											toClient2.writeBoolean(dualExit);
											System.out.println("\n--Player 1 and Player 2 Continue For The Next Game!--");


										} else {
											
											dualExit=true;
											toClient1.writeBoolean(dualExit);
											toClient2.writeBoolean(dualExit);
											toClient1.writeUTF("\nNo Opponent Found! \n\nConnection Terminated!");
											toClient2.writeUTF("\nNo Opponent Found! \n\nConnection Terminated!");
											System.out.println("\nPlayer1 and Player2 Exit the Game!");
											break;
											
										}

									} else if (Again.equalsIgnoreCase("y") && Again2.equalsIgnoreCase("n")) { //2


										toClient1.writeUTF("Player 2 ["+changeName.get(1)+"] want to quit the Game!\n\nDo You Want To Continue (Y)?: ");
										toClient2.writeUTF("Do you want to exit the Game (Y/N) ?: ");
										String enter = fromClient1.readUTF();
										String enter2 = fromClient2.readUTF();

										if (enter.equalsIgnoreCase("y") && enter2.equalsIgnoreCase("n")) {

											toClient1.writeBoolean(dualExit);
											toClient2.writeBoolean(dualExit);
											System.out.println("\n--Player 1 and Player 2 Continue For The Next Game!--");

										} else if (enter.equalsIgnoreCase("y") && enter2.equalsIgnoreCase("y")) {

											dualExit = true;
											toClient1.writeBoolean(dualExit);
											toClient2.writeBoolean(dualExit);
											toClient1.writeUTF("\nNo Opponent Found! \n\nConnection Terminated!");
											toClient2.writeUTF("\nNo Opponent Found! \n\nConnection Terminated!");
											System.out.println("\nPlayer1 and Player2 Exit the Game!");

											break;

										} else if (enter.equalsIgnoreCase("n") && enter2.equalsIgnoreCase("n")) {


											dualExit = true;
											toClient1.writeBoolean(dualExit);
											toClient2.writeBoolean(dualExit);
											toClient1.writeUTF("\nNo Opponent Found! \n\nConnection Terminated!");
											toClient2.writeUTF("\nNo Opponent Found! \n\nConnection Terminated!");
											System.out.println("\nPlayer1 and Player2 Exit the Game!");

											break;



										} else {

											dualExit = true;
											toClient1.writeBoolean(dualExit);
											toClient2.writeBoolean(dualExit);
											toClient1.writeUTF("\nCommand Not Found!, Set to (y)\nNo Opponent Found! \n\nConnection Terminated!");
											toClient2.writeUTF("\nCommand Not Found!, Set to (y)\nNo Opponent Found! \n\nConnection Terminated!");
											System.out.println("\nPlayer1 and Player2 Exit the Game!");

											break;

										}


									} else if (Again.equalsIgnoreCase("n") && Again2.equalsIgnoreCase("y")) { //3


										toClient2.writeUTF("Player 1 ["+changeName.get(0)+"] want to quit the Game! \n\nDo You Want To Continue (Y)?: ");
										toClient1.writeUTF("Do you want to exit the Game (Y/N) ?: ");

										String enter = fromClient1.readUTF();
										String enter2 = fromClient2.readUTF();

										if (enter.equalsIgnoreCase("n") && enter2.equalsIgnoreCase("y")) {

											toClient1.writeBoolean(dualExit);
											toClient2.writeBoolean(dualExit);
											System.out.println("\n--Player 1 and Player 2 Continue For The Next Game!--");


										} else if (enter.equalsIgnoreCase("y") && enter2.equalsIgnoreCase("y")) {

											dualExit = true;
											toClient1.writeBoolean(dualExit);
											toClient2.writeBoolean(dualExit);
											toClient1.writeUTF("\nNo Opponent Found! \n\nConnection Terminated!");
											toClient2.writeUTF("\nNo Opponent Found! \n\nConnection Terminated!");
											System.out.println("\nPlayer1 and Player2 Exit the Game!");

											break;

										} else if (enter.equalsIgnoreCase("n") && enter2.equalsIgnoreCase("n")) {


											dualExit = true;
											toClient1.writeBoolean(dualExit);
											toClient2.writeBoolean(dualExit);
											toClient1.writeUTF("\nNo Opponent Found! \n\nConnection Terminated!");
											toClient2.writeUTF("\nNo Opponent Found! \n\nConnection Terminated!");
											System.out.println("\nPlayer1 and Player2 Exit the Game!");

											break;



										} else {

											dualExit = true;
											toClient1.writeBoolean(dualExit);
											toClient2.writeBoolean(dualExit);
											toClient1.writeUTF("\nCommand Not Found!, Set to (y)\nNo Opponent Found!, Connection Terminated!");
											toClient2.writeUTF("\nCommand Not Found!, Set to (y)\nNo Opponent Found!, Connection Terminated!");
											System.out.println("\nPlayer1 and Player2 Exit the Game!");

											break;

										}



									} else if (Again.equalsIgnoreCase("n") && Again2.equalsIgnoreCase("n")) { //4

										toClient2.writeUTF("--Exiting From The Game!-- \n\nEnter 'Y' To continue: ");
										toClient1.writeUTF("--Exiting From The Game!-- \n\nEnter 'Y' To continue: ");
										String enter = fromClient1.readUTF();
										String enter2 = fromClient2.readUTF();
										
										if (enter.equalsIgnoreCase("y") && enter2.equalsIgnoreCase("y")) {
											dualExit = true;
											toClient1.writeBoolean(dualExit);
											toClient2.writeBoolean(dualExit);
											toClient1.writeUTF("\nNo Opponent Found! \n\nConnection Terminated!");
											toClient2.writeUTF("\nNo Opponent Found! \n\nConnection Terminated!");
											break;
										}
										else if (!enter.equalsIgnoreCase("y") || !enter2.equalsIgnoreCase("y")){
											dualExit = true;
											toClient1.writeBoolean(dualExit);
											toClient2.writeBoolean(dualExit);
											toClient1.writeUTF("\nCommand Not Found from one of the Player! (Set to 'Y')\n\nNo Opponent Found!, Connection Terminated!");
											toClient2.writeUTF("\nCommand Not Found from one of the Player! (Set to 'Y')\n\n No Opponent Found!, Connection Terminated!");
											break;
										} else {
											dualExit = true;
											toClient1.writeBoolean(dualExit);
											toClient2.writeBoolean(dualExit);
											toClient1.writeUTF("\nNo Opponent Found! \n\nConnection Terminated!");
											toClient2.writeUTF("\nNo Opponent Found! \n\nConnection Terminated!");
											break;
										}

									} 

								}//while		

								System.out.println("\nConnection Terminated!");
								break;

							} //while2

						} catch (Exception  e) {
							e.printStackTrace();
						}


					}}).start();



				sessions++;


			} //while1


			serverSocket.close();


		} catch (Exception e) {
			e.printStackTrace();
		}


	}

}
