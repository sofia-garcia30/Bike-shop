import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InventoryGrid } from './inventory-grid';

describe('InventoryGrid', () => {
  let component: InventoryGrid;
  let fixture: ComponentFixture<InventoryGrid>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InventoryGrid],
    }).compileComponents();

    fixture = TestBed.createComponent(InventoryGrid);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
