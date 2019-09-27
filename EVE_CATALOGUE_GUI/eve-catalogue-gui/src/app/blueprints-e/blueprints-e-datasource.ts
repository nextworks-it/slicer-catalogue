import { DataSource } from '@angular/cdk/collections';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { map } from 'rxjs/operators';
import { Observable, of as observableOf, merge } from 'rxjs';

// TODO: Replace this with your own data model type
export interface BlueprintsEItem {
  expBlueprintId: string;
  name: string;
  expBlueprintVersion: string;
  verticalService: string;
  sites: string[];
}

// TODO: replace this with real data from your application
const EXAMPLE_DATA: BlueprintsEItem[] = [
  {expBlueprintId: "6", name: 'Blueprint for AGV experiments in a factory with variable size', expBlueprintVersion: "1.0", verticalService: "ASTI AGV control and automation", sites: ["5TONIC/Madrid"]},
  {expBlueprintId: "21", name: 'Blueprint for media service experiments on high-speed trains', expBlueprintVersion: "0.6", verticalService: "TRENITALIA Smart Transport with on-board media services", sites: ["Turin"]},
  {expBlueprintId: "35", name: 'Blueprint for mobility service experiments applied to people counting', expBlueprintVersion: "0.3", verticalService: "Smart Turin: safety and environment monitoring for mobility service", sites: ["Turin"]}
];

/**
 * Data source for the BlueprintsE view. This class should
 * encapsulate all logic for fetching and manipulating the displayed data
 * (including sorting, pagination, and filtering).
 */
export class BlueprintsEDataSource extends DataSource<BlueprintsEItem> {
  data: BlueprintsEItem[] = EXAMPLE_DATA;
  paginator: MatPaginator;
  sort: MatSort;

  constructor() {
    super();
  }

  /**
   * Connect this data source to the table. The table will only update when
   * the returned stream emits new items.
   * @returns A stream of the items to be rendered.
   */
  connect(): Observable<BlueprintsEItem[]> {
    // Combine everything that affects the rendered data into one update
    // stream for the data-table to consume.
    const dataMutations = [
      observableOf(this.data),
      this.paginator.page,
      this.sort.sortChange
    ];

    return merge(...dataMutations).pipe(map(() => {
      return this.getPagedData(this.getSortedData([...this.data]));
    }));
  }

  /**
   *  Called when the table is being destroyed. Use this function, to clean up
   * any open connections or free any held resources that were set up during connect.
   */
  disconnect() {}

  /**
   * Paginate the data (client-side). If you're using server-side pagination,
   * this would be replaced by requesting the appropriate data from the server.
   */
  private getPagedData(data: BlueprintsEItem[]) {
    const startIndex = this.paginator.pageIndex * this.paginator.pageSize;
    return data.splice(startIndex, this.paginator.pageSize);
  }

  /**
   * Sort the data (client-side). If you're using server-side sorting,
   * this would be replaced by requesting the appropriate data from the server.
   */
  private getSortedData(data: BlueprintsEItem[]) {
    if (!this.sort.active || this.sort.direction === '') {
      return data;
    }

    return data.sort((a, b) => {
      const isAsc = this.sort.direction === 'asc';
      switch (this.sort.active) {
        case 'name': return compare(a.name, b.name, isAsc);
        case 'id': return compare(+a.expBlueprintId, +b.expBlueprintId, isAsc);
        default: return 0;
      }
    });
  }
}

/** Simple sort comparator for example ID/Name columns (for client-side sorting). */
function compare(a, b, isAsc) {
  return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}
