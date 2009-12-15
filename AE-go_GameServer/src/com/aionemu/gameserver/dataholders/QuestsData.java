/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.dataholders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.events.QuestEvent;

/**
 * @author MrPoke
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "quests")
public class QuestsData
{

	@XmlElement(name = "quest", required = true)
	protected List<QuestTemplate>		questsData;
	private Map<Integer, QuestTemplate>	questData	= new HashMap<Integer, QuestTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(QuestTemplate quest : questsData)
		{
			if(quest.getStartNpcId() != null)
				QuestEngine.getInstance().setNpcQuestData(quest.getStartNpcId()).addOnQuestStart(quest.getId());
			if(quest.getEndNpcId() != null)
				QuestEngine.getInstance().setNpcQuestData(quest.getEndNpcId()).addOnQuestEnd(quest.getId());
			for(QuestEvent event : quest.getOnKillEvent())
			{
				for(int id : event.getIds())
				{
					QuestEngine.getInstance().setNpcQuestData(id).addOnKillEvent(quest.getId());
				}
			}

			for(QuestEvent event : quest.getOnTalkEvent())
			{
				for(int id : event.getIds())
				{
					QuestEngine.getInstance().setNpcQuestData(id).addOnTalkEvent(quest.getId());
				}
			}
			questData.put(quest.getId(), quest);
		}
		questsData.clear();
		questsData = null;
	}

	public QuestTemplate getQuestById(int id)
	{
		return questData.get(id);
	}

	public int size()
	{
		return questData.size();
	}
}