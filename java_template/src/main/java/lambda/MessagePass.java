package lambda;

import com.amazonaws.regions.AwsRegionProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.invoke.LambdaFunction;
import com.amazonaws.services.lambda.invoke.LambdaInvokerFactory;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import saaf.Inspector;
import saaf.Response;
import java.util.HashMap;
import java.util.UUID;

/**
 * uwt.lambda_test::handleRequest
 *
 * @author Wes Lloyd
 * @author Robert Cordingly
 */
public class MessagePass implements RequestHandler<Request, HashMap<String, Object>> {

    static String nodedata = "";
    static int calls = 0;
    
    // Replace with bucketname below
    // This is the bucket where SAAF files will be stored
    static final String bucketname = "tcss562.mylogs.aaa";    
    
    /**
     * Lambda Function Handler
     * 
     * @param request Request POJO with defined variables from Request.java
     * @param context 
     * @return HashMap that Lambda will automatically convert into JSON.
     */
    public HashMap<String, Object> handleRequest(Request request, Context context) {
        int totalCalls = 0;
        
        //Collect inital data.
        Inspector inspector = new Inspector();
        inspector.inspectAll();
                
        //****************START FUNCTION IMPLEMENTATION*************************
        if (!request.getSleep())
            calls ++;
        
        // Pass data, only if this node is just now receieving it...
        final ObjectMapper mapper = new ObjectMapper();
        if ((request.getCurrentround() <= request.getRounds()) && (!nodedata.matches(request.getData())) && (!request.getSleep()))
        {
            // Persist the data locally
            nodedata=request.getData();
            
            if (request.getCurrentround() < request.getRounds()) {
                Request newRequest = new Request();
                newRequest.setCurrentround(request.getCurrentround()+1);
                newRequest.setRounds(request.getRounds());
                newRequest.setNodespread(request.getNodespread());
                newRequest.setData(request.getData());
                newRequest.setSleep(false);

                final MessagePassingService messagePassingService = LambdaInvokerFactory.builder()
                        .lambdaClient(AWSLambdaClientBuilder.defaultClient())
                        .build(MessagePassingService.class);   

//                *** EXAMPLE of calling Lambda function in an alternate REGION
//                AWSLambda al = AWSLambdaClientBuilder.standard().withRegion(Regions.US_EAST_2).defaultClient();
//                final MessagePassingService messagePassingService = LambdaInvokerFactory.builder()
//                        .lambdaClient(al)
//                        .build(MessagePassingService.class);   

                // TO DO
                // Make this multithreaded
                for (int i=0;i<request.getNodespread();i++)
                {
                    totalCalls = totalCalls + 1;
                    System.out.println("Nodespread " + i+1 + " of " + request.getNodespread()); 
                    HashMap<String, Object> newHM = messagePassingService.callMessagePassing(newRequest);
                    System.out.println("lambda function invoke complete");
                    System.out.println("function-response=" + newHM.toString());
                    totalCalls = totalCalls + Integer.parseInt(newHM.get("totalCalls").toString());
                }
            }
            
        }
        else
        {
            
            System.out.println("Round is " + request.getCurrentround() + " of " + request.getRounds());
            System.out.println("Request to pass data, but local node has data='" + nodedata + "'");
            try
            {
                if (request.getSleep())
                {
                    // The long sleep interval is used to initialize a set of function instances to
                    // receieve the data message...
                    System.out.println("SLEEEPING!!!");
                    Thread.sleep(12000);
                }
            }
            catch (InterruptedException ie)
            {
                System.out.println("Interrupted while sleeping in no data pass mode!");
            }
        }
        
        // Set return result in Response class, class is marshalled into JSON
        Response r = new Response();
        r.setValue(nodedata);
        r.setCalls(calls);
        r.setTotalCalls(totalCalls);
        
        
        //****************END FUNCTION IMPLEMENTATION***************************
        
        //Collect final information such as total runtime and cpu deltas.
        inspector.inspectAllDeltas();

        // EXAMPLE CODE for publishing SAAF output to an S3 bucket
        
        // Create unique filename
        String filename = UUID.randomUUID().toString() + ".json";
        
        // Serialize the JSON into a bytestream 
        byte[] bytes = null;
        try
        {
            bytes = mapper.writeValueAsBytes(inspector.finish());
        }
        catch (JsonProcessingException jpe)
        {
            System.out.println("Unable to convert SAAF HashMap to JSON for S3 output!");
            StringWriter sw = new StringWriter();
            sw.append("Unable to generate JSON");
            bytes = sw.toString().getBytes(StandardCharsets.UTF_8);
        }
        
        // Pass bytestream to input stream for output to S3 bucket
        InputStream is = new ByteArrayInputStream(bytes);
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(bytes.length);
        meta.setContentType("text/plain");

        // publish JSON to bucket
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
        s3Client.putObject(bucketname, filename, is, meta);        
        
        inspector.consumeResponse(r);
        return inspector.finish();
    }
    
    public interface MessagePassingService {

        @LambdaFunction(functionName = "tutorial9")
        HashMap<String, Object> callMessagePassing(Request input);
    }
}
