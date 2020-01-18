package it.polito.dp2.BIB.sol3.db;

import java.math.BigInteger;
import java.util.List;

import it.polito.dp2.BIB.sol3.service.jaxb.Bookshelf;
import it.polito.dp2.BIB.sol3.service.jaxb.Bookshelves;
import it.polito.dp2.BIB.sol3.service.jaxb.Item;

/**
 * An interface to interact with a DB of Item and Citation objects
 *
 */
public interface DBBS {
	
	
	/**
	 * Create a new bookshelf in the DB using the given item information
	 * @param item the item object with the information about the item to be created
	 * @return an integer id assigned to the created item
	 * @throws NullPointerException if item is null
	 * @throws Exception if the item cannot be created in the DB for other reasons
	 */
	String createBookShelf(Bookshelf bookshelf) throws Exception;

	//Bookshelf searchBookShelf(String name) throws Exception;
	
	Bookshelves searchBookShelves(String keyword) throws Exception;

	List<Item> getItems(String name) throws Exception;
	
	boolean addItem(Item item, String bookshelf) throws Exception;
	
	void deleteItem(Item i, String bookshelf) throws Exception;
	
	String deleteBookShelf(String bookshelf);

	void deleteItem(Item item) throws Exception;
	
	BigInteger getBookShelfReads(String name) throws Exception;

//	Bookshelf updateBookshelf(String name, Bookshelf bookshelf);
	void updateBookshelfItem(Item item);

	Bookshelf getBookShelf(String name);
	
	//void mapItem(String self, BigInteger id);
	//BigInteger getItemId(String self);

	
}