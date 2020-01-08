# Create and edit a Petri net

## Overview

Editing is controlled by the buttons on the right-hand side and by a 
pop-up menu. This can be opened by right-clicking on the workspace.

## Create elements

In the pop-up menu, select the menu item "Add element". A submenu opens 
in which the elements to be created can be selected. When adding 
places, you can choose between the three available types in 
another submenu.

After the menu selection the selected element "hangs" on the mouse 
arrow. A left click at the position where the element is to be placed 
completes the element creation process.

## Select elements

A left click on an element selects it. A left click on another element 
deselects the element again and selects the new element. A left click 
outside an element on the desktop deselects the currently selected 
element.

## Remove elements

Select the menu item "Remove element" from the pop-up menu. This 
removes the selected element. If no element is selected, the status bar 
of the simulator informs the user that no element could be removed 
because none was selected.

## Connect elements

First of all, a few things need to be taken into account here:
- The connection is always from output to input.
- The output of a transition must always be connected to the input of 
a digit and vice versa
- Inputs and outputs support multiple connections. This is not true for 
transitions.

So in the simplest case you move the mouse over an output of an 
element. This output changes its color from black to green. 
A left click now starts the connection process. Then move the mouse 
arrow over the input of an element of the other type (transition, 
place). Now the input pin also lights up green. A left click ends 
the connecting process and the arc is created.

### Add points

For more complex than straight output-to-input connections there is 
the possibility to set points. 

This can be done directly when connecting. You start connecting by 
clicking on a output. But not click the wanted input just yet. Instead 
a few left clicks can be made along the way - the last click should of 
course still point to an input pin to complete the connection. 

Each left click between output and input creates a point. These points 
can be moved as desired after connecting while holding down the left 
mouse button.

Points can also be added to an arc later. To do this, simply click 
with the left mouse button anywhere on the arc; a new point is 
created at the click point.

### Remove points

To remove a point, simply move it beyond the left edge of the display 
(not just of the application window). It will be deleted when the edge 
is reached.

## Remove arcs

A connection is released by clicking again on an already connected 
input or output of a transition. Since several arcs may lead away from 
places, the following special feature results: A click on the output 
of a place does not remove the arc, but starts a new connection. 
Therefore arcs are always removed by clicking on their transition-side 
connector.

## Edit properties

To do this, an element must be selected. Then click on the "Properties"
button or select "Properties" from the "Edit Element" pop-up menu. 
In the window that appears, properties of the respective element can 
be set. The properties of transitions can only be edited in 
disconnected state (no arcs).

## Edit weights

An element must also be selected here. However, this here must be a 
transition, other elements can not be assigned weights. Then click on 
the "Weights" button or select "Weights" from the "Edit Element" 
pop-up menu. In the appearing window the weights of the transition 
can be set. 

## New, Load, Save
Loading and saving are currently not supported. With 'New' the 
existing network is deleted and a new one can be created.

# Execute

## Single step

To execute the current Petri net, use the buttons on the right side. 
The "Single Step" button attempts to let the net execute one step. 
A step is considered successful if a change in value has occurred at 
at least one output point. A step is considered unexecutable if no 
transition can be calculated. Since these two conditions are checked 
directly in the algorithm

## run until deadlock

When this button is pressed, as many steps as possible are performed. 

After this function is finished, no further steps can be taken at first.

However, you can change the values at the positions, allowing for more 
steps to be performed soon.
