package demo39;

public class ForumServiceImpl implements ForumService{

	@Override
	public void removeTopic(int topicId) {
		 System.out.println("模拟删除Topic记录" + topicId);
		 try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void removeForum(int forumId) {
		 System.out.println("模拟删除Forum记录" + forumId);
		 try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
