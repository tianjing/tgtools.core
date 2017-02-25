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

	public ListTask(String p_Name) {
		this(p_Name, null);
	}

	public ListTask(String p_Name, Collection<? extends T> p_Tasks) {
		setName(p_Name);
		if (null != p_Tasks && p_Tasks.size() > 0) {
			super.addAll(p_Tasks);
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
	 * @param p_Name
	 */
	public void setName(String p_Name) {
		m_Name = p_Name;
	}

	private String m_Name;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 将一个任务集合添加到当前任务集合的末尾
	 * 
	 * @param p_ListTask
	 */
	public void Merge(ListTask<T> p_ListTask) {
		super.addAll(p_ListTask);
	}

}
