# Limit Order System

### Data structure chosen,TreeMap<Double, LinkedList<>>:

- The data structure was chosen because it provides the capability to sort elements
with a comparator. In this specific scenario we require to sort elements by their price.
- A linkedList was chosen here to keep orders in the manner they were added
- When an order is modified the date changes causing this item to lose priority. When 
we return items based on Sell/Buy we can use a comparator to sort them.


