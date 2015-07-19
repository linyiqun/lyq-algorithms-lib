package ConsistentHash;

/**
 * 测试实体类
 * @author lyq
 *
 */
public class Entity {
	//实体类名称
	String name;
	//实体对象的值
	int value;
	
	public Entity(String name, int value){
		this.name = name;
		this.value = value;
	}
}
