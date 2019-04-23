package searcher;

public class Url_power { //url+权重
	String url="";
	int power=0;
	public Url_power(String url){
		this.url=url;
		this.power=0;
	}
	
	public Url_power(String url,int power){
		this.url=url;
		this.power=power;
	}
	public String getUrl(){
		return url;
	}
	public int getPower(){
		return power;
	}
}
