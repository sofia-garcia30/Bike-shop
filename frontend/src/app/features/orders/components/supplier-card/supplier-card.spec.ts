import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplierCard } from './supplier-card';

describe('SupplierCard', () => {
  let component: SupplierCard;
  let fixture: ComponentFixture<SupplierCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SupplierCard],
    }).compileComponents();

    fixture = TestBed.createComponent(SupplierCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
