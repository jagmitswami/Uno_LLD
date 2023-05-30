package com.uno.game;

import com.uno.game.enums.Color;
import com.uno.game.enums.Value;

public class Card {

	private Color color;
	private Value value;
	
	/**/
	
	public Card(Color color, Value value) {
		super();
		this.color = color;
		this.value = value;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public Value getValue() {
		return value;
	}
	public void setValue(Value value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "Card [color=" + color.toString() + ", value=" + value.toString() + "]";
	}
	
}
