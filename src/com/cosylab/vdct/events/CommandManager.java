package com.cosylab.vdct.events;

import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (18.12.2000 15:26:40)
 * @author: Matej Sekoranja
 */
public class CommandManager {
	protected Hashtable commands;

	protected static CommandManager instance = null;
/**
 * CommandManager constructor comment.
 */
protected CommandManager() {
	commands = new Hashtable();
}
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 15:33:35)
 * @param id java.lang.String
 * @param command com.cosylab.vdct.events.Command
 */
public void addCommand(String id, Command command) {
	if (commands.containsKey(id))
		throw new IllegalArgumentException("Error: command with id '"+id+"' already exists...");
	else commands.put(id, command);
}
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 15:32:53)
 */
public void clear() {
	commands.clear();
}
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 15:35:33)
 * @param id java.lang.String
 */
public void execute(String id) {
	Command command = (Command)commands.get(id);
	if (command!=null) command.execute();
}
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 15:35:33)
 * @return com.cosylab.vdct.events.Command
 * @param id java.lang.String
 */
public Command getCommand(String id) {
	Command command = (Command)commands.get(id);
	return command;
}
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 15:30:35)
 * @return com.cosylab.vdct.events.CommandManager
 */
public static CommandManager getInstance() {
	if (instance==null)
		instance = new CommandManager();
	return instance;
}
/**
 * Insert the method's description here.
 * Creation date: (18.12.2000 15:35:01)
 * @param id java.lang.String
 */
public void removeCommand(String id) {
	commands.remove(id);
}
}
