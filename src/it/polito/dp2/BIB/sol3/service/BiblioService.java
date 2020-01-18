package it.polito.dp2.BIB.sol3.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.rowset.serial.SerialException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;

import it.polito.dp2.BIB.ass3.UnknownItemException;
import it.polito.dp2.BIB.sol3.db.BadRequestInOperationException;
import it.polito.dp2.BIB.sol3.db.ConflictInOperationException;
import it.polito.dp2.BIB.sol3.db.DB;
import it.polito.dp2.BIB.sol3.db.DBBS;
import it.polito.dp2.BIB.sol3.db.ItemPage;
import it.polito.dp2.BIB.sol3.db.Neo4jDB;
import it.polito.dp2.BIB.sol3.service.jaxb.Bookshelf;
import it.polito.dp2.BIB.sol3.service.jaxb.Bookshelves;
import it.polito.dp2.BIB.sol3.service.jaxb.Citation;
import it.polito.dp2.BIB.sol3.service.jaxb.Item;
import it.polito.dp2.BIB.sol3.service.jaxb.Items;
import it.polito.dp2.BIB.sol3.service.util.ResourseUtils;

public class BiblioService {
	private DB n4jDb = Neo4jDB.getNeo4jDB();
	private DBBS n4jDbBS = Neo4jDBE.getNeo4jDB(n4jDb);
	ResourseUtils rutil;


	public BiblioService(UriInfo uriInfo) {
		rutil = new ResourseUtils((uriInfo.getBaseUriBuilder()));
	}
	
	public Items getItems(SearchScope scope, String keyword, int beforeInclusive, int afterInclusive, BigInteger page) throws Exception {
		ItemPage itemPage = n4jDb.getItems(scope,keyword,beforeInclusive,afterInclusive,page);

		Items items = new Items();
		List<Item> list = items.getItem();
		
		Set<Entry<BigInteger,Item>> set = itemPage.getMap().entrySet();
		for(Entry<BigInteger,Item> entry:set) {
			Item item = entry.getValue();
			rutil.completeItem(item, entry.getKey());
			list.add(item);
		}
		items.setTotalPages(itemPage.getTotalPages());
		items.setPage(page);
		return items;
	}

	public Item getItem(BigInteger id) throws Exception {
			Item item = n4jDb.getItem(id);
			if (item!=null)
				rutil.completeItem(item, id);
			return item;
	}

	public Item updateItem(BigInteger id, Item item) throws Exception {
		Item ret = n4jDb.updateItem(id, item);
		//todo: update n4jdbe items
		if (ret!=null) {
			rutil.completeItem(item, id);
			n4jDbBS.updateBookshelfItem(item);
			return item;
		} else
			return null;
	}

	public Item createItem(Item item) throws Exception {
		BigInteger id = n4jDb.createItem(item);
		if (id==null)
			throw new Exception("Null id");
		rutil.completeItem(item, id);
		return item;
	}

	public BigInteger deleteItem(BigInteger id) throws ConflictServiceException, Exception {
		try {
			Item item = n4jDb.getItem(id);
			if(item != null){
				rutil.completeItem(item, id);
				n4jDbBS.deleteItem(item);
			}
			return n4jDb.deleteItem(id);
		} catch (ConflictInOperationException e) {
			throw new ConflictServiceException();
		}
	}

	public Citation createItemCitation(BigInteger id, BigInteger tid, Citation citation) throws Exception {
		try {
			return n4jDb.createItemCitation(id, tid, citation);
		} catch (BadRequestInOperationException e) {
			throw new BadRequestServiceException();
		}
	}

	public Citation getItemCitation(BigInteger id, BigInteger tid) throws Exception {
		Citation citation = n4jDb.getItemCitation(id,tid);
		if (citation!=null)
			rutil.completeCitation(citation, id, tid);
		return citation;
	}

	public boolean deleteItemCitation(BigInteger id, BigInteger tid) throws Exception {
		return n4jDb.deleteItemCitation(id, tid);
	}

	public Items getItemCitations(BigInteger id) throws Exception {
		ItemPage itemPage = n4jDb.getItemCitations(id, BigInteger.ONE);
		if (itemPage==null)
			return null;

		Items items = new Items();
		List<Item> list = items.getItem();
		
		Set<Entry<BigInteger,Item>> set = itemPage.getMap().entrySet();
		for(Entry<BigInteger,Item> entry:set) {
			Item item = entry.getValue();
			rutil.completeItem(item, entry.getKey());
			list.add(item);
		}
		items.setTotalPages(itemPage.getTotalPages());
		items.setPage(BigInteger.ONE);
		return items;
	}

	public Items getItemCitedBy(BigInteger id) throws Exception {
		ItemPage itemPage = n4jDb.getItemCitedBy(id, BigInteger.ONE);
		if (itemPage==null)
			return null;

		Items items = new Items();
		List<Item> list = items.getItem();
		
		Set<Entry<BigInteger,Item>> set = itemPage.getMap().entrySet();
		for(Entry<BigInteger,Item> entry:set) {
			Item item = entry.getValue();
			rutil.completeItem(item, entry.getKey());
			list.add(item);
		}
		items.setTotalPages(itemPage.getTotalPages());
		items.setPage(BigInteger.ONE);
		return items;
	}
	
	public String createBookShelf(Bookshelf bookshelf) throws Exception {
		String name = n4jDbBS.createBookShelf(bookshelf);
		if (name==null)
			throw new BadRequestException("Bad name");
		return name;
	}
	public Bookshelves getBookshelves(String keyword) throws Exception {
		Bookshelves item = n4jDbBS.searchBookShelves(keyword);
		return item;
	}
	public Bookshelf getBookshelf(String name){
		return n4jDbBS.getBookShelf(name);
	}
//	public List<Item> getBookshelfItems(String name) throws Exception {
//		return n4jDbBS.getItems(name);
//	}
	
	public void addBookShelfItem(String bookshelf, BigInteger id) throws Exception {
		Item i = n4jDb.getItem(id);
		if(i == null) //todo: gestire diversamente
			throw new UnknownItemException();
		rutil.completeItem(i, id);
		n4jDbBS.addItem(i, bookshelf);
	}

	public String deleteBookshelf(String name) {
		return n4jDbBS.deleteBookShelf(name);
	}

	public BigInteger getBookshelfReads(String name) throws Exception {
		return n4jDbBS.getBookShelfReads(name);
	}
	
//	private static BigInteger selfToBigIteger(String self){
//		String id_s = (self.split("items/"))[1];
//		Integer id = Integer.valueOf(id_s);
//		return BigInteger.valueOf(id);
//	}

	public void deleteBookshelfItem(String name, BigInteger id) throws Exception {
		Item i = n4jDb.getItem(id);
		if(i == null) //todo: gestire diversamente
			throw new NotFoundException();
		rutil.completeItem(i, id);

		n4jDbBS.deleteItem(i, name);
	}


}