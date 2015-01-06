
public class Car {
public String color;
public String ai;
public String speed;
public String column;
public String row;
public String number;
public boolean visible;
public Car(String color, String ai, String speed, String column, String row, int number)
{
	this.color=color;
	this.ai=ai;
	this.speed=speed;
	this.column=column;
	this.row=row;
	this.number=Integer.toString(number);
	visible=true;
	
}
}
