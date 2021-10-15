package io.javaalmanac.data.output;

class OutputStats {

	private int files = 0;

	private long bytes = 0L;

	void addFile(long bytes) {
		this.files++;
		this.bytes += bytes;
	}

	boolean hasFiles() {
		return files > 0;
	}

	@Override
	public String toString() {
		return String.format("%s files, total %s bytes", files, bytes);
	}

}
