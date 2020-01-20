package it.polito.dp2.BIB.sol3.resources;

import it.polito.dp2.BIB.sol3.service.jaxb.*;
import it.polito.dp2.BIB.sol3.service.ItemReaderImpl;
import it.polito.dp2.BIB.ass3.ServiceException;
import it.polito.dp2.BIB.sol3.model.EBiblio;
import it.polito.dp2.BIB.sol3.service.BadRequestServiceException;
import it.polito.dp2.BIB.sol3.service.BiblioService;
import it.polito.dp2.BIB.sol3.service.ConflictServiceException;
import it.polito.dp2.BIB.sol3.service.SearchScope;

import java.math.BigInteger;
import java.net.URI;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/biblio")
@Api(value = "/biblio")
public class BiblioResources {
	public UriInfo uriInfo;
	
	BiblioService service;

	public BiblioResources(@Context UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		this.service = new BiblioService(uriInfo);
	}
	
	@GET
    @ApiOperation(value = "getBiblio", notes = "read main resource"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response=Biblio.class),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public EBiblio getBiblio() {
		return new EBiblio(uriInfo.getAbsolutePathBuilder());
	}
	
	@GET
	@Path("/items")
    @ApiOperation(value = "getItems", notes = "search items"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response=Items.class),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Items getItems(
			@ApiParam("The keyword to be used for the search") @QueryParam("keyword") @DefaultValue("") String keyword,
			@ApiParam("The year before which items are searched") @QueryParam("beforeInclusive") @DefaultValue("10000") int beforeInclusive,
			@ApiParam("The year after which items are searched") @QueryParam("afterInclusive") @DefaultValue("0") int afterInclusive,
			@ApiParam("The page of results to be read") @QueryParam("page") @DefaultValue("1") int page
			) {
		try {
			return service.getItems(SearchScope.ALL, keyword, beforeInclusive, afterInclusive, BigInteger.valueOf(page));
		} catch (Exception e) {
			throw new InternalServerErrorException(e);
		}
	}

	@GET
	@Path("/items/articles")
    @ApiOperation(value = "getArticles", notes = "search articles"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response=Items.class),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Items getArticles(
			@ApiParam("The keyword to be used for the search") @QueryParam("keyword") String keyword,
			@ApiParam("The year before which items are searched") @QueryParam("beforeInclusive") @DefaultValue("10000") int beforeInclusive,
			@ApiParam("The year after which items are searched") @QueryParam("afterInclusive") @DefaultValue("0") int afterInclusive,
			@ApiParam("The page of results to be read") @QueryParam("page") @DefaultValue("1") int page
			) {
		if (keyword==null)
			throw new BadRequestException("keyword is required");
		try {
			return service.getItems(SearchScope.ARTICLES, keyword, beforeInclusive, afterInclusive, BigInteger.valueOf(page));
		} catch (Exception e) {
			throw new InternalServerErrorException(e);
		}	
	}

	@GET
	@Path("/items/books")
    @ApiOperation(value = "getBooks", notes = "search books"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response=Items.class),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Items getBooks(
			@ApiParam("The keyword to be used for the search") @QueryParam("keyword") String keyword,
			@ApiParam("The year before which items are searched") @QueryParam("beforeInclusive") @DefaultValue("10000") int beforeInclusive,
			@ApiParam("The year after which items are searched") @QueryParam("afterInclusive") @DefaultValue("0") int afterInclusive,
			@ApiParam("The page of results to be read") @QueryParam("page") @DefaultValue("1") int page
			) {
		if (keyword==null)
			throw new BadRequestException("keyword is required");
		try {
			return service.getItems(SearchScope.BOOKS, keyword, beforeInclusive, afterInclusive, BigInteger.valueOf(page));
		} catch (Exception e) {
			throw new InternalServerErrorException();
		}

	}

	@POST
	@Path("/items")
    @ApiOperation(value = "createItem", notes = "create a new item", response=Item.class
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 201, message = "OK", response=Item.class),
    		@ApiResponse(code = 400, message = "Bad Request"),
    		})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response createItem(Item item) {
		try {
			Item returnItem = service.createItem(item);
			return Response.created(new URI(returnItem.getSelf())).entity(returnItem).build();
		} catch (Exception e1) {
			throw new InternalServerErrorException();
		}
	}

	@GET
	@Path("/items/{id}")
    @ApiOperation(value = "getItem", notes = "read a single item"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response=Item.class),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Item getItem(
			@ApiParam("The id of the item") @PathParam("id") BigInteger id) {
		Item item;
		try {
			item = service.getItem(id);
		} catch (Exception e) {
			throw new InternalServerErrorException();
		}
		if (item==null)
			throw new NotFoundException();
		return item;
	}
	
	@PUT
	@Path("/items/{id}")
    @ApiOperation(value = "updateItem", notes = "update a single item"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response=Item.class),
    		@ApiResponse(code = 400, message = "Bad Request"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Item updateItem(
			@ApiParam("The id of the item") @PathParam("id") BigInteger id,
			Item item) {
		Item updated;
		try {
			updated = service.updateItem(id, item);
		} catch (Exception e) {
			throw new InternalServerErrorException();
		}
		if (updated==null)
			throw new NotFoundException();
		return updated;
	}
	
	@DELETE
	@Path("/items/{id}")
    @ApiOperation(value = "deleteItem", notes = "delete a single item"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 204, message = "No content"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		@ApiResponse(code = 409, message = "Conflict (item is cited)"),
    		})
	public void deleteItem(
			@ApiParam("The id of the item") @PathParam("id") BigInteger id) {
		BigInteger ret;
		try {
			ret = service.deleteItem(id);
		} catch (ConflictServiceException e) {
			throw new ClientErrorException(409);
		} catch (Exception e) {
			throw new InternalServerErrorException();
		}
		if (ret==null)
			throw new NotFoundException();
		return;
	}
	
	@GET
	@Path("/items/{id}/citedBy")
    @ApiOperation(value = "getItemCitedBy", notes = "read the items citing an item"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response=Items.class),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Items getItemCitedBy(
			@ApiParam("The id of the item for which citing items have to be read") @PathParam("id") BigInteger id) {
		Items items;
		try {
			items = service.getItemCitedBy(id);
		} catch (Exception e) {
			throw new InternalServerErrorException();
		}
		if (items==null)
			throw new NotFoundException();
		return items;
	}

	@GET
	@Path("/items/{id}/citations/targets")
    @ApiOperation(value = "getItemCitations", notes = "read the target items of the citations from an item"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response=Items.class),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Items getItemCitations(
			@ApiParam("The id of the item from which citations are considered") @PathParam("id") BigInteger id) {
		Items items;
		try {
			items = service.getItemCitations(id);
		} catch (Exception e) {
			throw new InternalServerErrorException();
		}
		if (items==null)
			throw new NotFoundException();
		return items;
	}

	@GET
	@Path("/items/{id}/citations/{tid}")
    @ApiOperation(value = "getItemCitation", notes = "read a citation"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response=Citation.class),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Citation getItemCitation(
			@ApiParam("The id of the citing item of this citation") @PathParam("id") BigInteger id,
			@ApiParam("The id of the cited item of this citation") @PathParam("tid") BigInteger tid) throws Exception {
		Citation citation;
		try {
			citation = service.getItemCitation(id,tid);
		} catch (Exception e) {
			throw new InternalServerErrorException();
		}
		if (citation==null)
			throw new NotFoundException();
		return citation;
	}

	@PUT
	@Path("/items/{id}/citations/{tid}")
    @ApiOperation(value = "createItemCitation", notes = "create a citation", response=Citation.class
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 201, message = "Created", response=Citation.class),
    		@ApiResponse(code = 400, message = "Bad Request"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		@ApiResponse(code = 409, message = "Conflict"),
    		})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response createItemCitation(
			@ApiParam("The id of the citing item of this citation") @PathParam("id") BigInteger id,
			@ApiParam("The id of the cited item of this citation") @PathParam("tid") BigInteger tid,
			Citation citation) throws Exception {
		UriBuilder builder = uriInfo.getAbsolutePathBuilder();
		System.out.println(uriInfo.getBaseUri());
    	URI u = builder.build();
    	UriBuilder fromBuilder = UriBuilder.fromUri(citation.getFrom());
    	URI u2 = fromBuilder.path("citations").path(tid.toString()).build();
	    if (!u.equals(u2))
	    	throw new BadRequestException();
	    citation.setSelf(u.toString());
	    Citation newCitation;
	    try {
	    	newCitation = service.createItemCitation(id, tid, citation);
	    } catch (BadRequestServiceException e) {
			throw new BadRequestException();
		} catch (ConflictServiceException e) {
			throw new ClientErrorException(409);
		} catch (Exception e) {
			throw new InternalServerErrorException();
		}	
		if (newCitation==null)
			throw new NotFoundException();
	    return Response.created(u).entity(newCitation).build();
	    	
	}
	
	@DELETE
	@Path("/items/{id}/citations/{tid}")
    @ApiOperation(value = "deleteItemCitation", notes = "delete a citation"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 204, message = "OK", response=Citation.class),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
	public void deleteItemCitation(
			@ApiParam("The id of the citing item of this citation") @PathParam("id") BigInteger id, 
			@ApiParam("The id of the cited item of this citation") @PathParam("tid") BigInteger tid) {
		boolean success;
		try {
			success=service.deleteItemCitation(id,tid);
		} catch (Exception e) {
			throw new InternalServerErrorException();
		}
		if(!success)
			throw new NotFoundException();
		return;
	}
	

	
	@POST
	@Path("/bookshelves")
    @ApiOperation(value = "createBookshelf", notes = "create a new bookshelf", response=String.class
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 201, message = "OK", response=String.class),
    		@ApiResponse(code = 400, message = "Bad Request"),
    		})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public String createBookShelf(Bookshelf bookshelf) {
		try {
			return service.createBookShelf(bookshelf);
		} catch (BadRequestException e){
			throw e;
		/* 
		 * @todo rimuovere internal server error
		 * @body gestire tutte le eccezioni generiche magari con una bad request, verificare cosa lancio
		 */
		} catch (Exception e1) {
			throw new InternalServerErrorException();
		}
	}
	
	@GET
	@Path("/bookshelves")
    @ApiOperation(value = "getBookshelves", notes = "search bookshelves"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response=Bookshelves.class),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Bookshelves getBookshelves(
			@ApiParam("The keyword to be used for the search") @QueryParam("keyword") @DefaultValue("") String keyword
			) {
		try {
			return service.getBookshelves(keyword);
		} catch (Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	@GET
	@Path("/bookshelves/{name}")
    @ApiOperation(value = "getBookshelf", notes = "read a single bookshelf"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response=Bookshelf.class),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Bookshelf getBookshelf(
			@ApiParam("The name of a bookshelf") @PathParam("name") String name) {
		Bookshelf bookshelf = service.getBookshelf(name);
		if (bookshelf==null)
			throw new NotFoundException();
		return bookshelf;
	}
	

	@DELETE
	@Path("/bookshelves/{name}")
    @ApiOperation(value = "deleteBookshelf", notes = "delete a single bookshelf"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 204, message = "No content"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
	public void deleteBookshelf(
			@ApiParam("The name of the bookshelf") @PathParam("name") String name) {
		String ret = service.deleteBookshelf(name);
		if (ret==null)
			throw new NotFoundException();
		return;
	}
	
	@POST
	@Path("/bookshelves/{name}/{id}")
    @ApiOperation(value = "addItem", notes = "add an item to the bookshelf", response=String.class
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 201, message = "OK"),
    		@ApiResponse(code = 400, message = "Bad Request: item not found"),
    		@ApiResponse(code = 404, message = "Not found: destroyed bookshelf"),
    		@ApiResponse(code = 406, message = "Not acceptable: too many items"),
    		})
//	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
//	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public String addItems(
			@ApiParam("The name of a 	bookshelf") @PathParam("name") String name,
			@ApiParam("The id of an item") @PathParam("id") BigInteger id) {
		try {
			service.addBookShelfItem(name, id);
		} catch (NotAcceptableException e1) {
			throw new NotAcceptableException(e1.getCause() + " " + e1.getMessage());
		} catch (BadRequestException e2) {
			throw new BadRequestException(e2.getCause() + " " + e2.getMessage());
		}
		catch (NotFoundException e3) {
			throw new NotFoundException(e3.getCause() + " " + e3.getMessage());
		}
		catch (Exception e) {
			throw new InternalServerErrorException(e.getCause() + " " + e.getMessage());
		}
		return id.toString();
	}
	
	@DELETE
	@Path("/bookshelves/{name}/{id}")
    @ApiOperation(value = "deleteBookshelfItem", notes = "delete a single item in a bookshelf"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 204, message = "No content"),
    		@ApiResponse(code = 400, message = "Bad request"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
	public void deleteBookshelfItem(
			@ApiParam("The name of the bookshelf") @PathParam("name") String name,
			@ApiParam("The id of an item") @PathParam("id") BigInteger id) {

		try {
			service.deleteBookshelfItem(name, id);
		} catch (NotFoundException e1) {
			throw e1;	
		} catch (Exception e2) {
			throw new BadRequestException();
		}
	}
	
	@GET
	@Path("/bookshelves/{name}/reads")
    @ApiOperation(value = "getBookshelf", notes = "reads of a bookshelf"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK", response=String.class),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public String getBookshelfReads(
			@ApiParam("Read numbers of a bookshelf") @PathParam("name") String name) {
		BigInteger readsNumber;
		try {
			readsNumber = service.getBookshelfReads(name);
		}catch (NotFoundException e1){
			throw e1;
		} catch (Exception e2) {
			throw new InternalServerErrorException();
		}
		return readsNumber.toString();
	}
	
}