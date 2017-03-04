package com.noahark.graffiti.snow;

public class Coordinate 
{
	public int x;
	public int y;

	public Coordinate(int newX, int newY) 
	{
		x = newX;
		y = newY;
	}

	@Override
	public String toString() 
	{
		return "Coordinate: [" + x + "," + y + "]";
	}
}
