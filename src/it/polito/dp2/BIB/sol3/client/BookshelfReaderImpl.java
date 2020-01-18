package it.polito.dp2.BIB.sol3.client;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import it.polito.dp2.BIB.ass3.DestroyedBookshelfException;
import it.polito.dp2.BIB.ass3.ItemReader;
import it.polito.dp2.BIB.ass3.ServiceException;
import it.polito.dp2.BIB.ass3.TooManyItemsException;
import it.polito.dp2.BIB.ass3.UnknownItemException;

public class BookshelfReaderImpl implements it.polito.dp2.BIB.ass3.Bookshelf{
	it.polito.dp2.BIB.sol3.client.Bookshelves.Bookshelf b;
	javax.ws.rs.client.Client client;
	WebTarget target;
	static String uri = "http://localhost:8080/BiblioSystem/rest";

	public BookshelfReaderImpl(it.polito.dp2.BIB.sol3.client.Bookshelves.Bookshelf i) {
		this.b = i;
		BookshelfReaderImpl.uri = uri.toString();
		client = ClientBuilder.newClient();
		target = client.target(uri).path("biblio");
	}
	
	@Override
	public String getName() throws DestroyedBookshelfException {
		if(b==null)
			throw new DestroyedBookshelfException();
		return b.getName();
	}

	@Override
	public void addItem(ItemReader item)
			throws DestroyedBookshelfException, UnknownItemException, TooManyItemsException, ServiceException {
		if(!(item instanceof ItemReaderImpl))
			throw new UnknownItemException("You have to use ItemReaderImpl");
		String id = target.path("/bookshelves/").
				path(getName()).
				path("1").
				request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(item), String.class);
		return;
	}

	@Override
	public void removeItem(ItemReader item) throws DestroyedBookshelfException, UnknownItemException, ServiceException {
		String self = ((ItemReaderImpl) item).getSelf();
		BigInteger id_d = selfToBigIteger(self);
		target.path("/bookshelves/").
				path(getName()).
				path(id_d.toString()).
				request(MediaType.APPLICATION_JSON_TYPE).delete();
	}

	@Override
	public Set<ItemReader> getItems() throws DestroyedBookshelfException, ServiceException {
		HashSet<ItemReader> set = new HashSet<>();
		Items items = target.path("/bookshelves/").
				path(getName()).
				request(MediaType.APPLICATION_JSON_TYPE).get(Items.class);		
		for(Items.Item item: items.getItem()){
			set.add(new ItemReaderImpl(item));
		}
		return set;
	}

	@Override
	public void destroyBookshelf() throws DestroyedBookshelfException, ServiceException {
		target.path("/bookshelves/").
		path(getName()).
		request(MediaType.APPLICATION_JSON_TYPE).delete();		
	}

	@Override
	public int getNumberOfReads() throws DestroyedBookshelfException {
		String num = target.path("/bookshelves/").
				path(getName()).path("/reads").
				request(MediaType.APPLICATION_JSON_TYPE).get(String.class);
		return Integer.valueOf(num);
	}
	
	private static BigInteger selfToBigIteger(String self){
		String id_s = (self.split("items/"))[1];
		Integer id = Integer.valueOf(id_s);
		return BigInteger.valueOf(id);
	}

}
