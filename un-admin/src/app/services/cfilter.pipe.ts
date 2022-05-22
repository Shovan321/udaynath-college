import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'cfilter',
})
export class CfilterPipe implements PipeTransform {
  transform(items: any, searchText: string) {
    if (searchText) {
      return items.filter((item) => {
        return item && item.rollMumber && ((item.rollMumber+'').toLowerCase().includes((searchText+'').toLowerCase()));
      });
    } else {
      return items;
    }
  }
}
