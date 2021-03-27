"""Collect Soak Data Script"""

import csv
import os
from pathlib import Path

# stable QPS
QPS = 150


if __name__ == '__main__':

    # cleanup before work
    for fph in Path('stats/soak').glob('*'):
        fph.unlink()

    # run for 1 hour as 120 intervals of 30 sec
    for i in range(120):
        print(f'Running Iteration #{i}')

        # run load testing
        os.system(
            f'locust -f locustfile.py --headless -u {QPS} -r {QPS} '
            f'--run-time 30s --csv stats/soak/{i} --only-summary'
        )

        # make correct file
        with open(f'stats/soak/{i}_stats.csv', 'r') as inp, open(f'stats/soak/{i}.csv', 'w') as out:
            writter = csv.writer(out)
            rows = tuple(csv.reader(inp))
            desired = {0, len(rows) - 1}
            for idx, row in enumerate(rows):
                if idx in desired:
                    writter.writerow(row)

        # remove redundant files
        for suffix in ('stats', 'exceptions', 'failures', 'stats_history'):
            Path(f'stats/soak/{i}_{suffix}.csv').unlink()

