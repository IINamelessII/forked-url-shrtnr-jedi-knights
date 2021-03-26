import matplotlib.pyplot as plt
import csv
import os
import re
from pathlib import Path


def convert_to_int(string: str):
    """
    Converts Locust statistics values to integer
    """
    return 0 if string == 'N/A' else int(string)


if __name__ == '__main__':

    # Parse all stats files and convert data to list of dicts
    BASE_DIR = Path(__file__).parent.absolute()
    STATS_PATH = f'{BASE_DIR}/stats/'

    raw_stats_list = []

    for filename in os.listdir(STATS_PATH):
        if re.match(".*.csv$", filename):
            with open(STATS_PATH + filename) as csv_file:
                reader = csv.DictReader(csv_file)

                dictobj = dict()
                try:
                    while True:
                        dictobj = next(reader)
                except StopIteration:
                    pass
                finally:
                    if dictobj:
                        # dictobj['Users/s'] = int(Path(filename).stem)
                        raw_stats_list.append(dictobj)
                    del reader

    # Convert general data to dict { <latency>: (<QPS>, <Response Time>) }
    latency_stats = {
        "50%": [],
        "90%": [],
        "99%": [],
    }

    for percentile in latency_stats.keys():
        latency_stats[percentile] = sorted([
            (round(float(item['Requests/s'])),
             convert_to_int(item[percentile]))
            for item in raw_stats_list
        ], key=lambda el: el[0])

    # Build line charts for each percentile
    fig, axs = plt.subplots(3)
    fig.suptitle('Load-testing results')

    fig.add_subplot(111, frameon=False)
    plt.tick_params(labelcolor='none', top=False,
                    bottom=False, left=False, right=False)
    plt.grid(False)

    plt.xlabel('QPS')
    plt.ylabel('Response Time')

    color = ['maroon', 'steelblue', 'seagreen']
    for (idx, (percentile, data)) in enumerate(latency_stats.items()):
        axs[idx].plot(*list(zip(*data)), marker='.', color=color[idx])
        axs[idx].set_title(percentile)
        axs[idx].grid()

    fig.tight_layout()
    plt.show()
