package com.example.demo.upload;

public class FileNotImageExeption extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7039937167325588114L;
	private static final String EXCEPTION_MESSAGE="File is not image";
	public FileNotImageExeption() {
		super(EXCEPTION_MESSAGE);
	};

}
