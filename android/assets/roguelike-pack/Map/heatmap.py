
		

		
import heapq

def convertGrid(grid, barrierVal, width, height):
    #print grid
    walls = []
    for col in range(0, width):
        for row in range(0, height):
            #print grid[row][col]
            if grid[row][col] == barrierVal: 
                walls.append((col, height-1-row))
    return walls

class Cell(object):
    def __init__(self, x, y, reachable):
        """Initialize new cell.
        @param reachable is cell reachable? not a wall?
        @param x cell x coordinate
        @param y cell y coordinate
        @param g cost to move from the starting cell to this cell.
        @param h estimation of the cost to move from this cell
                 to the ending cell.
        @param f f = g + h
        """
        self.reachable = reachable
        self.x = x
        self.y = y
        self.parent = None
        self.g = 0
        self.h = 0
        self.f = 0


class AStar(object):
    def __init__(self):
        # open list
        self.opened = []
        heapq.heapify(self.opened)
        # visited cells list
        self.closed = set()
        # grid cells
        self.cells = []
        self.grid_height = None
        self.grid_width = None

    def init_grid(self, width, height, walls, start, end):
        """Prepare grid cells, walls.
        @param width grid's width.
        @param height grid's height.
        @param walls list of wall x,y tuples.
        @param start grid starting point x,y tuple.
        @param end grid ending point x,y tuple.
        """
        self.grid_height = height
        self.grid_width = width
        for x in range(self.grid_width):
            for y in range(self.grid_height):
                if (x, y) in walls:
                    reachable = False
                else:
                    reachable = True
                self.cells.append(Cell(x, y, reachable))
        self.start = self.get_cell(*start)
        self.end = self.get_cell(*end)

    def get_heuristic(self, cell):
        """Compute the heuristic value H for a cell.
        Distance between this cell and the ending cell multiply by 10.
        @returns heuristic value H
        """
        return 10 * (abs(cell.x - self.end.x) + abs(cell.y - self.end.y))

    def get_cell(self, x, y):
        """Returns a cell from the cells list.
        @param x cell x coordinate
        @param y cell y coordinate
        @returns cell
        """
        return self.cells[x * self.grid_height + y]

    def get_adjacent_cells(self, cell):
        """Returns adjacent cells to a cell.
        Clockwise starting from the one on the right.
        @param cell get adjacent cells for this cell
        @returns adjacent cells list.
        """
        cells = []
        if cell.x < self.grid_width-1:
            cells.append(self.get_cell(cell.x+1, cell.y))
        if cell.y > 0:
            cells.append(self.get_cell(cell.x, cell.y-1))
        if cell.x > 0:
            cells.append(self.get_cell(cell.x-1, cell.y))
        if cell.y < self.grid_height-1:
            cells.append(self.get_cell(cell.x, cell.y+1))
        return cells

    def get_path(self):
        cell = self.end
        path = [(cell.x, cell.y)]
        while cell.parent is not self.start:
            cell = cell.parent
            path.append((cell.x, cell.y))

        path.append((self.start.x, self.start.y))
        path.reverse()
        return path

    def update_cell(self, adj, cell):
        """Update adjacent cell.
        @param adj adjacent cell to current cell
        @param cell current cell being processed
        """
        adj.g = cell.g + 10
        adj.h = self.get_heuristic(adj)
        adj.parent = cell
        adj.f = adj.h + adj.g

    def solve(self):
        """Solve maze, find path to ending cell.
        @returns path or None if not found.
        """
        # add starting cell to open heap queue
        heapq.heappush(self.opened, (self.start.f, self.start))
        while len(self.opened):
            # pop cell from heap queue
            f, cell = heapq.heappop(self.opened)
            # add cell to closed list so we don't process it twice
            self.closed.add(cell)
            # if ending cell, return found path
            if cell is self.end:
                return self.get_path()
            # get adjacent cells for cell
            adj_cells = self.get_adjacent_cells(cell)
            for adj_cell in adj_cells:
                if adj_cell.reachable and adj_cell not in self.closed:
                    if (adj_cell.f, adj_cell) in self.opened:
                        # if adj cell in open list, check if current path is
                        # better than the one previously found
                        # for this adj cell.
                        if adj_cell.g > cell.g + 10:
                            self.update_cell(adj_cell, cell)
                    else:
                        self.update_cell(adj_cell, cell)
                        # add adj cell to open list
                        heapq.heappush(self.opened, (adj_cell.f, adj_cell))


	
def getGrid(filename):
	inf = open(filename, 'r')
	lines = inf.readlines()
	grid = []
	inf.close()

	for i in range(33, 55):
		row = []
		line = lines[i]
		line = line.rstrip()
		line = line.lstrip()
		rowlist = line.split(',')   
		rowlist = [ x for x in rowlist if x.isdigit() ] 
		rowlist =  map(int, rowlist)
		grid.append(rowlist)
	return grid		


#walls = [(0, 5), (1, 0), (1, 1), (1, 5), (2, 3),
#		 (3, 1), (3, 2), (3, 5), (4, 1), (4, 4), (5, 1)]

barrierVal = 49
exitVal = -1

#a = AStar()
for i in range (0, 3):
	grid = getGrid('submaze_mars_'+str(i)+'.tmx')
	#print grid
	walls = convertGrid(grid, barrierVal, len(grid[0]), len(grid))
	grid[0][11] = exitVal
	distance_map = [[0 for i in range(len(grid[0]))] for j in range(len(grid))]
	#print distance_map
	dmap_str = '{'
	for row in range(0, len(grid)):
		dmap_str += '{'
		for col in range (0, len(grid[0])):
			#print row, col
			a = AStar()
			if grid[row][col] != barrierVal and grid[row][col] != exitVal: 
				a.init_grid(len(grid[0]), len(grid), walls, (col, len(grid) - 1 - row), (11, 21))
				path = a.solve()
				#print path
				distance = len(path)
				#print distance
				distance_map[row][col] = distance
			if col != (len(grid[0]) -1):
				dmap_str += str(distance_map[row][col]) + ','
			else: 
				dmap_str += str(distance_map[row][col]) 	
		if row != (len(grid) -1):
			dmap_str += '},\n'
		else: 
			dmap_str += '}'
	dmap_str += '}'
	print dmap_str
	print '\n\n'
