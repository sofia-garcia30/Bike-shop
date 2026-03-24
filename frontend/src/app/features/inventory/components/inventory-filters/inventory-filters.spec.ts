import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InventoryFilters } from './inventory-filters';

describe('InventoryFilters', () => {
  let component: InventoryFilters;
  let fixture: ComponentFixture<InventoryFilters>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InventoryFilters],
    }).compileComponents();

    fixture = TestBed.createComponent(InventoryFilters);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
