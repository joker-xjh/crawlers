package demo65;

public enum testEnum {
	
	PLUS{
		@Override
		double eval(double x, double y) {
			return x + y;
		}
	},
	
	MINUS{
		@Override
		double eval(double x, double y) {
			return x - y;
		}	
	},
	
	TIMES{

		@Override
		double eval(double x, double y) {
			return x * y;
		}
		
	},
	
	DIVIDE{
		@Override
		double eval(double x, double y) {
			return x / y;
		}
	};
	
	
	abstract double eval(double x, double y);

}
