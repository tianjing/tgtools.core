package tgtools.tasks;

import java.util.ArrayList;
import java.util.Collection;
/**
 * 描述了一个 Task 集合
 * @author tianjing
 *
 * @param <T>
 */
public  class ListTask<T extends Task> extends ArrayList<T> {

	public ListTask() {
		this("", null);
	}

	public ListTask(String pName) {
		this(pName, null);
	}

	public ListTask(String pName, Collection<? extends T> pTasks) {
		setName(pName);
		if (null != pTasks && pTasks.size() > 0) {
			super.addAll(pTasks);
		}
	}

	/**
	 * 获取任务集合的名称
	 * 
	 * @return
	 */
	public String getName() {
		return m_Name;
	}

	/**
	 * 设置任务集合的名称
	 * 
	 * @param pName
	 */
	public void setName(String pName) {
		m_Name = pName;
	}

	private String m_Name;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 将一个任务集合添加到当前任务集合的末尾
	 * 
	 * @param pListTask
	 */
	public void Merge(ListTask<T> pListTask) {
		super.addAll(pListTask);
	}

}
