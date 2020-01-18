package it.polito.dp2.BIB.sol3.client;

import java.net.URI;
import java.net.URISyntaxException;

import it.polito.dp2.BIB.ass3.Client;
import it.polito.dp2.BIB.ass3.ClientException;

public class ClientFactory extends it.polito.dp2.BIB.ass3.ClientFactory{

	@Override
	public Client newClient() throws ClientException {
		URI  uri = null;
		try {
			//todo: property port?????
			String uriProp = System.getProperty("it.polito.dp2.BIB.ass3.URL");
			System.out.println("property: "+uriProp);
			if(uriProp!=null) {
				uri= new URI(uriProp);
			}else {
				uri= new URI("http://localhost:8080/RnsSystem/rest/");
			}
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return new ClientFactoryImpl(uri);
	}

}
