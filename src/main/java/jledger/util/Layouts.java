package jledger.util;

import jledger.core.Content;

public class Layouts {
	
	
	public static Content.Path LEAF(int index) {
		return new Content.Path() {
			
			@Override
			public int index() {
				return index;
			}
			
			@Override
			public Content.Path child() {
				throw new IllegalArgumentException();
			}
		};
	}
	
	public static Content.Path NODE(int index, Content.Path child) {
		return new Content.Path() {
			
			@Override
			public int index() {
				return index;
			}
			
			@Override
			public Content.Path child() {
				return child;
			}
		};
	}
	
	
	public static Content.Layout STATIC(Content.Layout... children) {
		return new Content.Layout() {

			@Override
			public void write_u1(boolean value, Content.Path path) {
				// TODO Auto-generated method stub

			}

			@Override
			public void write_u32(int value, Content.Path path) {
				// TODO Auto-generated method stub

			}

		};
	}
	
	/**
	 * Construct a layout whose children repeat a given layout 0 or more times.
	 * 
	 * @param child
	 * @return
	 */
	public static Content.Layout REPEAT(Content.Layout child) {
		return new Content.Layout() {

			@Override
			public void write_u1(boolean value, int... path) {
				// TODO Auto-generated method stub

			}

			@Override
			public void write_u32(int value, int... path) {
				// TODO Auto-generated method stub

			}

		};
	}
	
}
