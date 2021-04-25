package ManualRetry.src.main.java.manualRetry;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.lang.IllegalStateException;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestStreamHandler {

    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        LambdaLogger logger = context.getLogger();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("US-ASCII")));
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, Charset.forName("US-ASCII"))));
        
        try{
            HashMap event = gson.fromJson(reader, HashMap.class);
            // log execution details
            logger.log("ENVIRONMENT VARIABLES: " + gson.toJson(System.getenv()));
            logger.log("CONTEXT: " + gson.toJson(context));
            // process event
            logger.log("EVENT: " + gson.toJson(event));

            Map<String, String> response = new HashMap<>();
            response.put("sfid", "sfid1234");
            writer.write(gson.toJson(response));

            //Take Task Token and save to Dynamo {Account - Message - Status - Token}
            //Notify customer (SES/SNS)
            //When fixed, use https://awscli.amazonaws.com/v2/documentation/api/latest/reference/stepfunctions/send-task-success.html to restart

        }
        catch (IllegalStateException | JsonSyntaxException exception)
        {
            logger.log(exception.toString());
        }
        finally
        {
            reader.close();
            writer.close();
        }
    }
}
