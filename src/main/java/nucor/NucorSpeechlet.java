/**
    Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

        http://aws.amazon.com/apache2.0/

    or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package nucor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.Card;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;

public class NucorSpeechlet implements Speechlet {
	
	private static final HashMap<String, String> nucorCastripTotalRevenue = new HashMap<String, String>();
	
	private static final  Map<String, String> clarkDietRichTotalRevenue = new HashMap<String, String>();
	
	private static final  Map<String, String> totalRevenues = new HashMap<String, String>();
        
        private static final  Map<String, String> revenue = new HashMap<String, String>();
                     	
	private static final  Map<String, String> nucorCastripTotalProfit = new HashMap<String, String>();
	
	private static final Logger log = LoggerFactory.getLogger(NucorSpeechlet.class);

	/**
	 * The slots defined in Intent.
	 */
	private static final String SLOT_YEAR = "YEARNO";

	private static final String SLOT_CITY = "CITY";

	private static final String SLOT_COUNTRY = "COUNTRY";

	private static final String SLOT_STATE = "STATE";

	private static final String SLOT_CUSTOMER = "CUSTOMER";
	

	static{
		nucorCastripTotalRevenue.put("2011", "882824.16");
		nucorCastripTotalRevenue.put("2012", "7207544.88");
		
		clarkDietRichTotalRevenue.put("2011", "459919.99");
		clarkDietRichTotalRevenue.put("2012", "6764786.66");
		
		totalRevenues.put("2011", "37120503.89");
		totalRevenues.put("2012", "1447366272");
		totalRevenues.put("clarkdietrich", "40491578.87");
		totalRevenues.put("nucor", "16433559.24");
		totalRevenues.put("Arkansas", "287893378.2");
		totalRevenues.put("Nevada", "10004161.28");
		
		nucorCastripTotalProfit.put("nucor2011", "563214.78");
		nucorCastripTotalProfit.put("nucor", "563214.78");
                nucorCastripTotalProfit.put("nucor2012", "563214.78");
                nucorCastripTotalProfit.put("clarkdietrich", "563214.78");
                nucorCastripTotalProfit.put("clarkdietrich2012", "563214.78");
                nucorCastripTotalProfit.put("clarkdietrich2011", "563214.78");
                
                revenue.put("nucor2012", "7207544.88");
                revenue.put("nucor2011", "882824.16");
                revenue.put("clarkdietrich2011", "459919.99");
                revenue.put("clarkdietrich2012", "6764786.66");
                revenue.put("clarkdietrich2012", "6764786.66");
                
                revenue.put("clarkdietrich2012nevada", "");
                revenue.put("clarkdietrich2012arkansas", "44786.66");
                 revenue.put("nucor2012neveda", ".88");
                revenue.put("nucor2011arkansas", "882824.16");
	}
	@Override
	public void onSessionStarted(final SessionStartedRequest request, final Session session) throws SpeechletException {
		log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

	}

	@Override
	public SpeechletResponse onLaunch(final LaunchRequest request, final Session session) throws SpeechletException {
		log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

		String speechOutput = "Hello there! Welcome to the Nucor. How can i help you?";

		String repromptText = "Do you want me to wait! Please say yes or no!";

		// Here we are prompting the user for input
		return newAskResponse(speechOutput, false, "<speak>" + repromptText + "</speak>", true);
	}

	@Override
	public SpeechletResponse onIntent(final IntentRequest request, final Session session) throws SpeechletException {
		log.info("onIntent requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

		Intent intent = request.getIntent();
		String intentName = (intent != null) ? intent.getName() : null;

		if ("TotalRevenue".equals(intentName)) {
			return getTotalRevenueResponse(intent, session);
		} else if ("TotalProfit".equals(intentName)) {
			return getTotalProfitResponse(intent, session);
		} else if ("MostProfitable".equals(intentName)) {
			return getMostProfitableResponse(intent, session);
		} else if ("AvgSpend".equals(intentName)) {
			return getAvgSpendResponse(intent, session);
		} else if ("Capacity".equals(intentName)) {
			return getCapacityResponse(intent, session);
		} else if ("HighCost".equals(intentName)) {
			return getHighCost(intent, session);
		} else if ("HighRebate".equals(intentName)) {
			return getHighRebate(intent, session);
		} else if ("LowCost".equals(intentName)) {
			return getLowCost(intent, session);
		} else if ("LowRebate".equals(intentName)) {
			return getLowRebate(intent, session);
		} else if ("SteelMillCapacity".equals(intentName)) {
			return getSteelMillCapacityResponse(intent, session);
		} else if ("HearMore".equals(intentName)) {
			return getMoreHelp();
		} else if ("DontHearMore".equals(intentName)) {
			PlainTextOutputSpeech output = new PlainTextOutputSpeech();
			output.setText("Thanks, Please do come again....");
			return SpeechletResponse.newTellResponse(output);
		} else if ("AMAZON.HelpIntent".equals(intentName)) {
			return getHelp();
		} else if ("AMAZON.PauseIntent".equals(intentName)) {
			return getWait();
		} else if ("AMAZON.StopIntent".equals(intentName)) {
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText("Bye,  Hope to see you soon!");
			// connUtil.closeConnection();
			return SpeechletResponse.newTellResponse(outputSpeech);
		} else if ("AMAZON.CancelIntent".equals(intentName)) {
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText("Goodbye! ");
			// connUtil.closeConnection();
			return SpeechletResponse.newTellResponse(outputSpeech);
		} else {
			// Reprompt the user.
			String speechOutput = "I'm sorry I didn't understand that. Please try again, intent " + intentName;

			String repromptText = "I'm sorry I didn't understand that. You can ask things like, "
					+ "what is the capacity of the mill <break time=\"0.2s\" /> "
					+ "Give me the total revenue for 2011 <break time=\"0.2s\" /> "
					+ "Tell me the total revenue for AR <break time=\"0.2s\" /> "
					+ "What is the profit for customer NUCOR CASTRIP for year 2011 <break time=\"0.2s\" /> "
					+ "Give me the total revenue for 2012 and for CLARKDIETRICH ";

			return newAskResponse(speechOutput, false, "<speak>" + repromptText + "</speak>", true);

		}
	}

	@Override
	public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
		log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

	}

	/**
	 * Creates a Query for Total Revenue based on the year/state/customer,
	 * executes the Query and returns the result.
	 *
	 * @param intent
	 *            the intent for the request
	 * @param session
	 * @return SpeechletResponse spoken and visual response for the given intent
	 * @throws SpeechletException
	 * 
	 */
	private SpeechletResponse getTotalRevenueResponse(final Intent intent, final Session session)
			throws SpeechletException {

		// Simple Display Card
		SimpleCard card = new SimpleCard();
		card.setTitle("Total Revenue ::");

		String yearNo = intent.getSlot(SLOT_YEAR) != null ? intent.getSlot(SLOT_YEAR).getValue() : "";

		String state = intent.getSlot(SLOT_STATE) != null ? intent.getSlot(SLOT_STATE).getValue() : "";

		String customer = intent.getSlot(SLOT_CUSTOMER) != null ? intent.getSlot(SLOT_CUSTOMER).getValue() : "";

		String answer = "The Total Revenue for ";

		String speechOut = "";

		String finalSpeechOut = "";

		String cardOut = "";

		String finalCardOut = "";

		String totalRevenue = "0.0";
		
		if (StringUtils.isNotEmpty(yearNo) && StringUtils.isEmpty(customer) && StringUtils.isEmpty(state)) {
			speechOut += "year <say-as interpret-as=\"cardinal\">" + yearNo + "</say-as>";
			cardOut += " year " + yearNo;
			if (totalRevenues.containsKey(yearNo)){
				totalRevenue = totalRevenues.get(yearNo);
			}
		}

		if (StringUtils.isNotEmpty(state) && StringUtils.isEmpty(yearNo) && StringUtils.isEmpty(customer)) {
			speechOut += " state " + state;
			cardOut += " state " + state;
			if (totalRevenues.containsKey(state)){
				totalRevenue = totalRevenues.get(state);
			}
		}

		if (StringUtils.isNotEmpty(customer) && StringUtils.isEmpty(state) && StringUtils.isEmpty(yearNo)) {
			speechOut += " customer " + customer;
			cardOut += " customer " + customer;
			if (totalRevenues.containsKey(customer)){
				totalRevenue = totalRevenues.get(customer);
			}
		}
                
                

		if (StringUtils.isNotEmpty(customer) && StringUtils.isEmpty(state) && StringUtils.isNotEmpty(yearNo)) {
			speechOut += " customer " + customer +"for"+ yearNo;
			cardOut += " customer " + customer;
			
                        String key=customer+yearNo;
                        
                        totalRevenue=revenue.get(key);
                        						
		}
		
		if (StringUtils.isNotEmpty(customer) && StringUtils.isNotEmpty(state) && StringUtils.isNotEmpty(yearNo)) {
			speechOut += " customer " + customer + "for"+ yearNo+ "for"+state;
			cardOut += " customer " + customer;
			
                        String key=customer+yearNo+state;
                        
                        totalRevenue=revenue.get(key);
                        						
		}
		finalSpeechOut = answer + speechOut + " is <break time=\"0.2s\" /> <say-as interpret-as=\"cardinal\"> "
				+ totalRevenue + "</say-as>";

		finalCardOut = answer + cardOut + " is " + totalRevenue;

		card.setContent(finalCardOut);

		String repromptText = " Would you like to hear more ? Please say yes or no";

		return newAskResponse("<speak>" + finalSpeechOut + "</speak>", true, "<speak>" + repromptText + "</speak>",
				true, card);
	}

	/**
	 * Creates a Dynamic Query for Total Profit based on the
	 * year/customer, executes the Query and returns the result
	 *
	 * @param intent
	 *            the intent for the request
	 * @param session
	 * @return SpeechletResponse spoken and visual response for the given intent
	 * @throws SpeechletException
	 * 
	 */
	private SpeechletResponse getTotalProfitResponse(final Intent intent, final Session session)
			throws SpeechletException {

		// Simple Display Card
		SimpleCard card = new SimpleCard();
		card.setTitle("Total Profit :: ");

		String yearNo = intent.getSlot(SLOT_YEAR) != null ? intent.getSlot(SLOT_YEAR).getValue() : "";

		String country = intent.getSlot(SLOT_COUNTRY) != null ? intent.getSlot(SLOT_COUNTRY).getValue() : "";

		String customer = intent.getSlot(SLOT_CUSTOMER) != null ? intent.getSlot(SLOT_CUSTOMER).getValue() : "";

		String answer = "The Total Profit for ";

		String speechOut = "";

		String finalSpeechOut = "";

		String cardOut = "";

		String finalCardOut = "";

		String totalProfit = "0.0";
		
		if (StringUtils.isNotEmpty(yearNo) && StringUtils.isNotEmpty(customer)) {
			speechOut += "customer " + customer + " and for year "
					+ "<say-as interpret-as=\"cardinal\">" + yearNo + "</say-as>";
			cardOut += "customer " + customer +" and for year " + yearNo + " /n";
			
                        String key=customer+yearNo;
                        totalProfit = nucorCastripTotalProfit.get(key);
			
		}

		if (StringUtils.isNotEmpty(country)) {
			speechOut += " country " + country;
			cardOut += " country " + country;
                        totalProfit = nucorCastripTotalProfit.get(country);
		}

		if (StringUtils.isNotEmpty(customer)) {
			speechOut += " customer " + customer;
			cardOut += " customer " + customer;
                        totalProfit = nucorCastripTotalProfit.get(customer);
		}

		
		finalSpeechOut = answer + speechOut + " is <break time=\"0.2s\" /> <say-as interpret-as=\"cardinal\"> "
				+ totalProfit + "</say-as>";

		finalCardOut = answer + cardOut + " is " + totalProfit;

		card.setContent(finalCardOut);

		String repromptText = " Would you like to hear more ? Please say yes or no";

		return newAskResponse("<speak>" + finalSpeechOut + "</speak>", true, "<speak>" + repromptText + "</speak>",
				true, card);
	}

	/**
	 * Creates a Dynamic Query for Total ROI based on the year/brand/customer,
	 * executes the Query and returns the result.
	 *
	 * @param intent
	 *            the intent for the request
	 * @param session
	 * @return SpeechletResponse spoken and visual response for the given intent
	 * @throws SpeechletException
	 * 
	 */
	private SpeechletResponse getMostProfitableResponse(final Intent intent, final Session session)
			throws SpeechletException {

		// Simple Display Card
		SimpleCard card = new SimpleCard();
		card.setTitle(" Most Profitable  :: ");

		String yearNo = intent.getSlot(SLOT_YEAR) != null ? intent.getSlot(SLOT_YEAR).getValue() : "";

		String state = intent.getSlot(SLOT_STATE) != null ? intent.getSlot(SLOT_STATE).getValue() : "";

		String customer = intent.getSlot(SLOT_CUSTOMER) != null ? intent.getSlot(SLOT_CUSTOMER).getValue() : "";

		String answer = "The Most Profitable for ";

		String speechOut = "";

		String finalSpeechOut = "";

		String cardOut = "";

		String finalCardOut = "";

		if (StringUtils.isNotEmpty(yearNo)) {
			speechOut += "year <say-as interpret-as=\"cardinal\">" + yearNo + "</say-as>";
			cardOut += " year " + yearNo + " /n";
		}

		if (StringUtils.isNotEmpty(state)) {
			speechOut += " state " + state;
			cardOut += " state " + state;
		}

		if (StringUtils.isNotEmpty(customer)) {
			speechOut += " customer " + customer;
			cardOut += " customer " + customer;
		}

		String totalROI = "260.3";

		finalSpeechOut = answer + speechOut + " is <break time=\"0.2s\" /> <say-as interpret-as=\"cardinal\"> "
				+ totalROI + "</say-as>";

		finalCardOut = answer + cardOut + " is " + totalROI;

		card.setContent(finalCardOut);

		String repromptText = " Would you like to hear more ? Please say yes or no";

		return newAskResponse("<speak>" + finalSpeechOut + "</speak>", true, "<speak>" + repromptText + "</speak>",
				true, card);
	}

	private SpeechletResponse getAvgSpendResponse(final Intent intent, final Session session)
			throws SpeechletException {

		// Simple Display Card
		SimpleCard card = new SimpleCard();
		card.setTitle(" Most Profitable  :: ");

		String yearNo = intent.getSlot(SLOT_YEAR) != null ? intent.getSlot(SLOT_YEAR).getValue() : "";

		String state = intent.getSlot(SLOT_STATE) != null ? intent.getSlot(SLOT_STATE).getValue() : "";

		String customer = intent.getSlot(SLOT_CUSTOMER) != null ? intent.getSlot(SLOT_CUSTOMER).getValue() : "";

		String answer = "The Most Profitable for ";

		String speechOut = "";

		String finalSpeechOut = "";

		String cardOut = "";

		String finalCardOut = "";

		if (StringUtils.isNotEmpty(yearNo)) {
			speechOut += "year <say-as interpret-as=\"cardinal\">" + yearNo + "</say-as>";
			cardOut += " year " + yearNo + " /n";
		}

		if (StringUtils.isNotEmpty(state)) {
			speechOut += " state " + state;
			cardOut += " state " + state;
		}

		if (StringUtils.isNotEmpty(customer)) {
			speechOut += " customer " + customer;
			cardOut += " customer " + customer;
		}

		String totalROI = "260.3";

		finalSpeechOut = answer + speechOut + " is <break time=\"0.2s\" /> <say-as interpret-as=\"cardinal\"> "
				+ totalROI + "</say-as>";

		finalCardOut = answer + cardOut + " is " + totalROI;

		card.setContent(finalCardOut);

		String repromptText = " Would you like to hear more ? Please say yes or no";

		return newAskResponse("<speak>" + finalSpeechOut + "</speak>", true, "<speak>" + repromptText + "</speak>",
				true, card);
	}

	private SpeechletResponse getCapacityResponse(final Intent intent, final Session session)
			throws SpeechletException {

		String speechOutput = " The Capacity of the Mill is <say-as interpret-as=\"cardinal\"> 574 "
				+ "</say-as>";

		String cardOut = "The Capacity of the Mill is 574 ";

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();

		card.setTitle(":: Mill Capacity::");

		card.setContent(cardOut);

		String repromptText = " Would you like to hear more ? Please say yes or no";

		return newAskResponse("<speak>" + speechOutput + "</speak>", true, repromptText, false, card);
	}
	
	private SpeechletResponse getHighCost(final Intent intent, final Session session)
			throws SpeechletException {

		String speechOutput = " The product with the highest cost is <say-as interpret-as=\"cardinal\"> HOT ROLLED PICKLE and OIL " +"</say-as>" 
		+"and the cost is 25949498.5";
				
		String cardOut = "Product with Highest Cost";

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();

		card.setTitle(":: Hight Cost::");

		card.setContent(cardOut);

		String repromptText = " Would you like to hear more ? Please say yes or no";

		return newAskResponse("<speak>" + speechOutput + "</speak>", true, repromptText, false, card);
	}
	
	private SpeechletResponse getLowCost(final Intent intent, final Session session)
			throws SpeechletException {

		String speechOutput = " The product with the lowest cost is <say-as interpret-as=\"cardinal\"> CGLVTX" 
				+ "</say-as>"  + "and the cost is 1232.14";

		String cardOut = "Product with Lowest Cost";

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();

		card.setTitle(":: Lowest Cost::");

		card.setContent(cardOut);

		String repromptText = " Would you like to hear more ? Please say yes or no";

		return newAskResponse("<speak>" + speechOutput + "</speak>", true, repromptText, false, card);
	}
	
	private SpeechletResponse getHighRebate(final Intent intent, final Session session)
			throws SpeechletException {
		String year = intent.getSlot(SLOT_YEAR) != null ? intent.getSlot(SLOT_YEAR).getValue() : "";
			
		String speechOutput="";
					
		if("2015".equals(year)) {speechOutput = " The customer with highest rebate in " + year + " is HEIDTMAN STEEL ";}
		
		else if("2016".equals(year)) {speechOutput = " The customer with highest rebate in " + year + " is COILPLUS INC ";}
        
		String cardOut = "Product with Highest Rebate";
		
		// Create the Simple card content.
		SimpleCard card = new SimpleCard();

		card.setTitle(":: Highest Rebate::");

		card.setContent(cardOut);

		String repromptText = " Would you like to hear more ? Please say yes or no";

		return newAskResponse("<speak>" + speechOutput + "</speak>", true, repromptText, false, card);
	}

	private SpeechletResponse getLowRebate(final Intent intent, final Session session)
			throws SpeechletException {
		String year = intent.getSlot(SLOT_YEAR) != null ? intent.getSlot(SLOT_YEAR).getValue() : "";			
		
	    String speechOutput = "The customer with lowest rebate in " +"year <say-as interpret-as=\"cardinal\"> " + year + "</say-as> is <break time=\"0.2s\" /> MAVERICK TUBE CORP";
        
		String cardOut = "Product with Lowest Rebate";

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();

		card.setTitle(":: Lowest Rebate::");

		card.setContent(cardOut);

		String repromptText = " Would you like to hear more ? Please say yes or no";

		return newAskResponse("<speak>" + speechOutput + "</speak>", true, repromptText, false, card);
	}
	
	private SpeechletResponse getSteelMillCapacityResponse(final Intent intent, final Session session)
			throws SpeechletException {

		String speechOutput = " <say-as interpret-as=\"cardinal\"> 574 </say-as> out of "
				+ "<say-as interpret-as=\"cardinal\"> 3450 </say-as> Promotions did not perform well";

		String cardOut = "574 out of 3450 Promotions did not performed well.";

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();

		card.setTitle(":: Promotions Performance for year 2016 ::");

		card.setContent(cardOut);

		String repromptText = " Would you like to hear more ? Please say yes or no";

		return newAskResponse("<speak>" + speechOutput + "</speak>", true, repromptText, false, card);
	}

	/**
	 * Instructs the user on how to interact with this skill.
	 */
	
	private SpeechletResponse getWait(){
		String speechOutput="You can ask a question when you are ready <break time=\"5s\" />";
						
		String repromptText="You can ask a question when you are ready <break time=\"0.8s\" />";
		
		return newAskResponse("<speak>"+ speechOutput + "</speak>", true, "<speak>" + repromptText + "</speak>", true);
	}
	
	private SpeechletResponse getHelp() {

		String speechOutput = "You can ask for the things like following <break time=\"0.2s\" />"
				+ "Give me the total revenue for 2016 Nucor<break time=\"0.2s\" /> "
				+ "Which product has the highest cost <break time=\"0.2s\" /> "
				+ "Which customer has the highest rebates";


		String repromptText = "I'm sorry I didn't understand that. You can ask things like,"
				+ "Which product has the highest cost <break time=\"0.2s\" /> "
				+ " Or you can say exit. Now, what can I help you with?";

		return newAskResponse("<speak>" + speechOutput + "</speak>", true, "<speak>" + repromptText + "</speak>", true);
	}

	/**
	 * Provides more help on how to interact with this skill.
	 */
	private SpeechletResponse getMoreHelp() throws SpeechletException {

		String speechOutput = "Please ask a question!";

		String repromptText = "Here are few sample questions, "
				+ "Give me the total revenue for 2016 Nucor<break time=\"0.2s\" /> "
				+ "Which product has the highest cost <break time=\"0.2s\" /> "
				+ "Which customer has the highest rebates";

		// Here we are prompting the user for input
		return newAskResponse(speechOutput, false, "<speak>" + repromptText + "</speak>", true);
	}

	/**
	 * Wrapper for creating the Ask response from the input strings.
	 * 
	 * @param stringOutput
	 *            the output to be spoken
	 * @param isOutputSsml
	 *            whether the output text is of type SSML
	 * @param repromptText
	 *            the reprompt for if the user doesn't reply or is
	 *            misunderstood.
	 * @param isRepromptSsml
	 *            whether the reprompt text is of type SSML
	 * @param displayCard
	 *            the display text to be sent to device
	 * @return SpeechletResponse the speechlet response
	 */
	private SpeechletResponse newAskResponse(String stringOutput, boolean isOutputSsml, String repromptText,
			boolean isRepromptSsml, Card displayCard) {
		OutputSpeech outputSpeech, repromptOutputSpeech;
		if (isOutputSsml) {
			outputSpeech = new SsmlOutputSpeech();
			((SsmlOutputSpeech) outputSpeech).setSsml(stringOutput);
		} else {
			outputSpeech = new PlainTextOutputSpeech();
			((PlainTextOutputSpeech) outputSpeech).setText(stringOutput);
		}

		if (isRepromptSsml) {
			repromptOutputSpeech = new SsmlOutputSpeech();
			((SsmlOutputSpeech) repromptOutputSpeech).setSsml(repromptText);
		} else {
			repromptOutputSpeech = new PlainTextOutputSpeech();
			((PlainTextOutputSpeech) repromptOutputSpeech).setText(repromptText);
		}
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(repromptOutputSpeech);
		return SpeechletResponse.newAskResponse(outputSpeech, reprompt, displayCard);
	}

	/**
	 * Wrapper for creating the Ask response from the input strings.
	 * 
	 * @param stringOutput
	 *            the output to be spoken
	 * @param isOutputSsml
	 *            whether the output text is of type SSML
	 * @param repromptText
	 *            the reprompt for if the user doesn't reply or is
	 *            misunderstood.
	 * @param isRepromptSsml
	 *            whether the reprompt text is of type SSML
	 * @return SpeechletResponse the speechlet response
	 */
	private SpeechletResponse newAskResponse(String stringOutput, boolean isOutputSsml, String repromptText,
			boolean isRepromptSsml) {
		OutputSpeech outputSpeech, repromptOutputSpeech;
		if (isOutputSsml) {
			outputSpeech = new SsmlOutputSpeech();
			((SsmlOutputSpeech) outputSpeech).setSsml(stringOutput);
		} else {
			outputSpeech = new PlainTextOutputSpeech();
			((PlainTextOutputSpeech) outputSpeech).setText(stringOutput);
		}

		if (isRepromptSsml) {
			repromptOutputSpeech = new SsmlOutputSpeech();
			((SsmlOutputSpeech) repromptOutputSpeech).setSsml(repromptText);
		} else {
			repromptOutputSpeech = new PlainTextOutputSpeech();
			((PlainTextOutputSpeech) repromptOutputSpeech).setText(repromptText);
		}
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(repromptOutputSpeech);
		return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
	}
}
