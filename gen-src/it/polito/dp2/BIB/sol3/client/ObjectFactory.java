
package it.polito.dp2.BIB.sol3.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.polito.dp2.BIB.sol3.client package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Biblio_QNAME = new QName("", "biblio");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.polito.dp2.BIB.sol3.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link it.polito.dp2.BIB.sol3.client.Bookshelf }
     * 
     */
    public it.polito.dp2.BIB.sol3.client.Bookshelf createBookshelf() {
        return new it.polito.dp2.BIB.sol3.client.Bookshelf();
    }

    /**
     * Create an instance of {@link Bookshelves }
     * 
     */
    public Bookshelves createBookshelves() {
        return new Bookshelves();
    }

    /**
     * Create an instance of {@link Items }
     * 
     */
    public Items createItems() {
        return new Items();
    }

    /**
     * Create an instance of {@link Bookshelves.Bookshelf }
     * 
     */
    public Bookshelves.Bookshelf createBookshelvesBookshelf() {
        return new Bookshelves.Bookshelf();
    }

    /**
     * Create an instance of {@link it.polito.dp2.BIB.sol3.client.Bookshelf.Item }
     * 
     */
    public it.polito.dp2.BIB.sol3.client.Bookshelf.Item createBookshelfItem() {
        return new it.polito.dp2.BIB.sol3.client.Bookshelf.Item();
    }

    /**
     * Create an instance of {@link Citation }
     * 
     */
    public Citation createCitation() {
        return new Citation();
    }

    /**
     * Create an instance of {@link it.polito.dp2.BIB.sol3.client.Item }
     * 
     */
    public it.polito.dp2.BIB.sol3.client.Item createItem() {
        return new it.polito.dp2.BIB.sol3.client.Item();
    }

    /**
     * Create an instance of {@link ArticleType }
     * 
     */
    public ArticleType createArticleType() {
        return new ArticleType();
    }

    /**
     * Create an instance of {@link BookType }
     * 
     */
    public BookType createBookType() {
        return new BookType();
    }

    /**
     * Create an instance of {@link Items.Item }
     * 
     */
    public Items.Item createItemsItem() {
        return new Items.Item();
    }

    /**
     * Create an instance of {@link Bookshelves.Bookshelf.Item }
     * 
     */
    public Bookshelves.Bookshelf.Item createBookshelvesBookshelfItem() {
        return new Bookshelves.Bookshelf.Item();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "biblio")
    public JAXBElement<Object> createBiblio(Object value) {
        return new JAXBElement<Object>(_Biblio_QNAME, Object.class, null, value);
    }

}
