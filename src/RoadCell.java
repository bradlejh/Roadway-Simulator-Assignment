public class RoadCell {

	public String row;
	public String column;
	public String type;
	public String degree;
	public boolean isLeft;
	public boolean isRight;
	public boolean isTop;
	public boolean isBottom;
	public RoadCell()
	{
	}
	
	public RoadCell(String row, String column, String type, String degree)
	{
		this.row=row;
		this.column=column;
		this.type=type;
		this.degree=degree;
		//Left Square Section
		System.out.print(row+column);
		if((type.equals("i")&&(degree.equals("90")||degree.equals("270"))) || (type.equals("l")&&(degree.equals("180")||degree.equals("90"))) || (type.equals("t")&&(degree.equals("0")||degree.equals("180")||degree.equals("270"))) || type.equals("+"))
		{
			System.out.println("isLeft");
			isLeft=true;
		}
		else
		{
			isLeft=false;
			System.out.println("is not left");
		}
		//Right Square Section
		if((type.equals("i")&&(degree.equals("90")||degree.equals("270"))) || (type.equals("l")&&(degree.equals("0")||degree.equals("270"))) || (type.equals("t")&&(degree.equals("0")||degree.equals("180")||degree.equals("90"))) || type.equals("+"))
		{
			isRight=true;
		}
		else
		{
			isRight=false;
		}
		//top square section
		if((type.equals("i")&&(degree.equals("0")||degree.equals("180"))) || (type.equals("l")&&(degree.equals("0")||degree.equals("90"))) || (type.equals("t")&&(degree.equals("90")||degree.equals("180")||degree.equals("270"))) || type.equals("+"))
		{
			isTop=true;
		}
		else
		{
			isTop=false;
		}
		//Bottom square section
		if((type.equals("i")&&(degree.equals("0")||degree.equals("180"))) || (type.equals("l")&&(degree.equals("180")||degree.equals("270"))) || (type.equals("t")&&(degree.equals("90")||degree.equals("0")||degree.equals("270"))) || type.equals("+"))
		{
			isBottom=true;
		}
		else
		{
			isBottom=false;
		}
	}
}