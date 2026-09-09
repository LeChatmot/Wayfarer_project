// item-lists-page.component.ts
import { Component, OnInit, inject, signal } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import {ItemListService} from '../../../core/services/item-list.service';
import {Item, ItemList} from '../../../core/models/item-list.model';
import {CreateListDialog} from '../create-list-dialog/create-list-dialog';
import {AddItemDialog} from '../add-item-dialog/add-item-dialog';

@Component({
  selector: 'app-item-lists-page',
  standalone: true,
  imports: [MatTableModule, MatCheckboxModule, MatButtonModule, MatIconModule, MatDialogModule, MatExpansionModule],
  templateUrl: './item-list-page.html',
  styleUrl: './item-list-page.scss'
})
export class ItemListsPage implements OnInit {
  private itemListService = inject(ItemListService);
  private dialog = inject(MatDialog);

  lists = signal<ItemList[]>([]);
  displayedColumns = ['checked', 'name', 'quantity', 'actions'];
  private checkedItemIds = new Set<number>();

  ngOnInit(): void {
    this.loadLists();
  }

  loadLists(): void {
    this.itemListService.getMyLists().subscribe(lists => this.lists.set(lists));
  }

  openCreateListDialog(): void {
    this.dialog.open(CreateListDialog, { width: '400px' })
      .afterClosed()
      .subscribe(name => {
        if (!name) return;
        this.itemListService.createList(name).subscribe(() => this.loadLists());
      });
  }

  openAddItemDialog(list: ItemList): void {
    this.dialog.open(AddItemDialog, { width: '400px' })
      .afterClosed()
      .subscribe(result => {
        if (!result) return;
        this.itemListService.addItem(list.id, result.name, result.quantity)
          .subscribe(() => this.loadLists());
      });
  }

  removeItem(list: ItemList, itemId: number): void {
    this.itemListService.removeItem(list.id, itemId).subscribe(() => {
      this.checkedItemIds.delete(itemId);
      this.loadLists();
    });
  }

  openEditItemDialog(list: ItemList, item: Item): void {
    this.dialog.open(AddItemDialog, { width: '400px', data: item })
      .afterClosed()
      .subscribe(result => {
        if (!result) return;
        this.itemListService.updateItem(list.id, item.id, result.name, result.quantity)
          .subscribe(() => this.loadLists());
      });
  }

  deleteList(list: ItemList): void {
    this.itemListService.deleteList(list.id).subscribe(() => this.loadLists());
  }

  toggleChecked(itemId: number, checked: boolean): void {
    if (checked) {
      this.checkedItemIds.add(itemId);
    } else {
      this.checkedItemIds.delete(itemId);
    }
  }

  isChecked(itemId: number): boolean {
    return this.checkedItemIds.has(itemId);
  }
}
