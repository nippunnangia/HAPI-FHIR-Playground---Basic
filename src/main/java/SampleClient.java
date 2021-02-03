import ca.uhn.fhir.context.FhirContext; // Starting point for API. Create only once.
import ca.uhn.fhir.rest.client.api.IGenericClient; // Interface IGenericClient
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor; // Client Logging Interceptor

import org.hl7.fhir.r4.model.Bundle; // Container for Collection of Resources
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient; // Demographics and other administrative information about an individual or animal receiving care or other health-related services

import java.util.*;

public class SampleClient {

    public static void main(String[] theArgs) {

        // Create a FHIR client
        FhirContext fhirContext = FhirContext.forR4(); // API instance
        IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
        client.registerInterceptor(new LoggingInterceptor(false));

        // Search for Patient resources
        Bundle response = client
                .search() // Search for resource matching a given set of criteria
                .forResource("Patient") // query (String "resource name")
                .where(Patient.FAMILY.matches().value("SMITH")) // query (search parameter)
                .returnBundle(Bundle.class) // request the client to return the specified bundle (Class<B> theClass)
                .execute(); // execute the client operation

        LinkedList<String> list = new LinkedList<String>(); // List for holding PATIENT strings

        for (int i = 0; response.getEntry().size() > i; i++){ // Loop over the bundle and get each entry
            Patient pat = (Patient) response.getEntry().get(i).getResource(); // Patient is the child of Resource so, cast upwards
            HumanName name = pat.getName().get(0); // Used to access the name from Patient
            
            // Store "not found" for missing birthdays instead of null
            if (pat.getBirthDate() == null){
                list.add("Name: " + name.getGivenAsSingleString() + " " + name.getFamily() + " BirthDate: not found");
            }
            else{
            list.add("Name: " + name.getGivenAsSingleString() + " " + name.getFamily() + " BirthDate: " + pat.getBirthDate());
            }
        }

        Collections.sort(list, String.CASE_INSENSITIVE_ORDER); // Sort the list by FirstName while ignoring case

        // Iterate over and the print the list
        Iterator<String> iterator = list.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }

    }

}

