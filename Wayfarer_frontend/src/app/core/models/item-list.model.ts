export interface Item {
  id: number;
  name: string;
  quantity: number | null;
}

export interface ItemList {
  id: number;
  name: string;
  items: Item[];
}
