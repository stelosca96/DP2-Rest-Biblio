package it.polito.dp2.BIB.sol3.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;

import it.polito.dp2.BIB.sol3.db.DB;
import it.polito.dp2.BIB.sol3.db.DBBS;
import it.polito.dp2.BIB.sol3.service.jaxb.Bookshelf;
import it.polito.dp2.BIB.sol3.service.jaxb.Bookshelves;
import it.polito.dp2.BIB.sol3.service.jaxb.Item;

public class Neo4jDBE implements DBBS{
	private Map<String, Bookshelf> bs;
	// private Map<String, BigInteger> itemMap;

	private static Neo4jDBE n4jDbE = null;
//	private Map<Bookshelf, String> sb;

	private Neo4jDBE(DB n4jDb) {
		bs = new ConcurrentHashMap<>();
		// itemMap = new ConcurrentHashMap<>();
	}
	
	public synchronized static Neo4jDBE getNeo4jDB(DB n4jDb){
		if(n4jDbE == null)
			n4jDbE = new Neo4jDBE(n4jDb);
		return n4jDbE;
	}
	
	@Override
	synchronized public String createBookShelf(Bookshelf bookshelf) throws Exception {
		String name = bookshelf.getName();
		if(name == null)
			return null;
		if(bs.containsKey(name))
			throw new BadRequestException("Bookshelf already exist");
		bs.put(name, bookshelf);
		return name;
	}
	
	@Override
	public Bookshelves searchBookShelves(String keyword) throws Exception {
		Bookshelves bookshelvesO = new Bookshelves();
		List<Bookshelf> bookshelves = bookshelvesO.getBookshelf();
		for(Bookshelf b: bs.values()){
			if(b.getName().contains(keyword))
				bookshelves.add(b);
		}
		return bookshelvesO;
	}


	@Override
	public synchronized List<Item> getItems(String name) throws Exception {
		BigInteger count = bs.get(name).getReadNumbers().add(BigInteger.ONE);
		bs.get(name).setReadNumbers(count);
		return bs.get(name).getItem();
	}

	@Override
	public synchronized boolean addItem(Item item, String bookshelf) throws Exception {
		if(bs.get(bookshelf) == null)
			throw new NotFoundException();
		for(Item i: bs.get(bookshelf).getItem()){
			if(i.getSelf().equals(item.getSelf()))
				throw new BadRequestException("Bookshelf already contains this element");
		}
		if(bs.get(bookshelf).getItem().size()>=20)
			throw new NotAcceptableException("Too many items inside the library");
		return bs.get(bookshelf).getItem().add(item);
	}

	@Override
	public synchronized void deleteItem(Item item, String name) throws Exception {
		Bookshelf bookshelf = bs.get(name);
		if(bookshelf == null)
			throw new NotFoundException("Bookshelf not found");
		for(int c = 0; c<bookshelf.getItem().size(); c++){
			if(bookshelf.getItem().get(c).getSelf().equals(item.getSelf())){
				bookshelf.getItem().remove(c);
				return;
			}
		}			
		throw new NotFoundException("Item not in bookshelf");
	}

	@Override
	public synchronized String deleteBookShelf(String bookshelf) {
		if(!bs.containsKey(bookshelf))
			return null;
		Bookshelf b = bs.remove(bookshelf);
		return b.getName();
	}

	@Override
	public synchronized void deleteItem(Item item) throws Exception {
		for(Bookshelf bookshelf: bs.values()){
			for(int c = 0; c<bookshelf.getItem().size(); c++){
				if(bookshelf.getItem().get(c).getSelf().equals(item.getSelf())){
					bookshelf.getItem().remove(c);
				}
			}			
		}
	}

	@Override
	public BigInteger getBookShelfReads(String name) throws Exception {
		if(bs.get(name) == null)
			throw new NotFoundException();
		return bs.get(name).getReadNumbers();
	}


//	@Override
//	public Bookshelf updateBookshelf(String name, Bookshelf bookshelf) {
//		// TODO Auto-generated method stub
//		return null;
//	}


	@Override
	public synchronized Bookshelf getBookShelf(String name){
		if(!bs.containsKey(name))
			return null;
		BigInteger readsNumber = bs.get(name).getReadNumbers();
		bs.get(name).setReadNumbers(readsNumber.add(BigInteger.ONE));
		return bs.get(name);
	}

	@Override
	public synchronized void updateBookshelfItem(Item item) {
		for(Bookshelf bookshelf: bs.values()){
			for(int c = 0; c<bookshelf.getItem().size(); c++){
				if(bookshelf.getItem().get(c).getSelf().equals(item.getSelf())){
					bookshelf.getItem().remove(c);
					bookshelf.getItem().add(item);

				}
			}		
		}
	}


}
