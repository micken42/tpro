package de.htw_berlin.tpro.plugin.bookstore;

import java.util.List;

import javax.inject.Inject;

import de.htw_berlin.tpro.framework.Start;
import de.htw_berlin.tpro.framework.Stop;
import de.htw_berlin.tpro.plugin.bookstore.model.Book;
import de.htw_berlin.tpro.plugin.bookstore.persistence.BookFacade;
import de.htw_berlin.tpro.plugin.bookstore.persistence.DefaultBookFacade;

public class MainClass
{
	
	@Inject @DefaultBookFacade
	BookFacade bookFacade;
	
	String name = "";
	
    public MainClass()
    {
    	name = "Bookstore";
        System.out.println(name + " installed!");
    }
    
    public String getName() {
    	return name;
    }
    
    @Start
    public void start1()
    {
    	List<Book> books = bookFacade.getAllBooks();
    	books.forEach(book -> System.out.println(book.getTitle()));
        System.out.println("app1.start1()");
    }
    
    public void start2()
    {
        System.out.println("app1.start2()");
    }
    
    @Start
    public int start3()
    {
        System.out.println("app1.start3()");
        return 0;
    }
    
    @Start
    public void start4(int i)
    {
        System.out.println("app1.start4(int)");
    }
    
    @Start
    protected void start5()
    {
        System.out.println("app1.start5()");
    }
    
    @Start
    public void start6()
    {
    	List<Book> books = bookFacade.getAllBooks();
    	books.forEach(book -> System.out.println(book.getTitle()));
        System.out.println("app1.start6()");
    }
    
    @Stop
    public void stop1()
    {
        System.out.println("app1.stop1()");
    }
    
    @Stop
    public int stop2()
    {
        System.out.println("app1.stop2()");
        return 0;
    }
    
    @Stop
    public void stop3()
    {
        System.out.println("app1.stop3()");
    }
}

