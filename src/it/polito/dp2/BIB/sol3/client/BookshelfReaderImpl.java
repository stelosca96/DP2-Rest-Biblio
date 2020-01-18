package it.polito.dp2.BIB.sol3.client;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import it.polito.dp2.BIB.ass3.DestroyedBookshelfException;
import it.polito.dp2.BIB.ass3.ItemReader;
import it.polito.dp2.BIB.ass3.ServiceException;
import it.polito.dp2.BIB.ass3.TooManyItemsException;
import it.polito.dp2.BIB.ass3.UnknownItemException;

public class BookshelfReaderImpl implements it.polito.dp2.BIB.ass3.Bookshelf{
	it.polito.dp2.BIB.sol3.client.Bookshelves.Bookshelf bookshelf;
	javax.ws.rs.client.Client client;
	WebTarget target;
	static String uri = "http://localhost:8080/BiblioSystem/rest";

	public BookshelfReaderImpl(it.polito.dp2.BIB.sol3.client.Bookshelves.Bookshelf i) {
		this.bookshelf = i;
		BookshelfReaderImpl.uri = uri.toString();
		client = ClientBuilder.newClient();
		target = client.target(uri).path("biblio");
	}
	
	@Override
	public String getName() throws DestroyedBookshelfException {
		if(bookshelf==null)
			throw new DestroyedBookshelfException();
		return bookshelf.getName();
	}

	@Override
	public void addItem(ItemReader item)
			throws DestroyedBookshelfException, UnknownItemException, TooManyItemsException, ServiceException {
		if(!(item instanceof ItemReaderImpl))
			throw new UnknownItemException("You have to use ItemReaderImpl");
		ItemReaderImpl i = (ItemReaderImpl) item;
//		try{
		 Response resp = target.path("/bookshelves/").
				path(getName()).
				path(selfToBigIteger(i.getSelf()).toString()).
				request(MediaType.APPLICATION_JSON_TYPE).post(null);
		Integer statusCode = resp.getStatus();
		switch (statusCode) {
			case 400:
				throw new UnknownItemException("Element " + selfToBigIteger(i.getSelf()) + " not found into db");
			case 404:
				throw new DestroyedBookshelfException();
			case 406:
				 throw new TooManyItemsException();
			default:
				break;
		}
		
//		}catch(NotAcceptableException e1){
//			 throw new TooManyItemsException();
//		}
//		catch (NotFoundException e2) {
//			throw new DestroyedBookshelfException();
//		}
//		catch (BadRequestException e3) {
//			throw new UnknownItemException("Elemento " + selfToBigIteger(i.getSelf()) + " non trovato");
//		}
	}

	@Override
	public void removeItem(ItemReader item) throws DestroyedBookshelfException, UnknownItemException, ServiceException {
		String self = ((ItemReaderImpl) item).getSelf();
		BigInteger id_d = selfToBigIteger(self);
		Response resp =target.path("/bookshelves/").
				path(getName()).
				path(id_d.toString()).
				request(MediaType.APPLICATION_JSON_TYPE).delete();
		if(resp.getStatus()==404)
			throw new DestroyedBookshelfException();
		 /*
	 	@todo: gestire elemento non presente nella bookshelf
	 	@body: se un elemento non è presente nella bookshelf lancio una bad request, probabilmente devo cambiare la cosa
	 	
	 */
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
