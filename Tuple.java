

public class Tuple {
		private String key=null;
		private String value=null;

		
		public tuple(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return this.key;
		}

		public String getValue() {
			return this.value;
		}
		public void setKey(String key)
		{
			if (key == null)
				throw new NullPointerException("Key cannot be  null.");
			else
				this.key = key;
		}
		public void setValue(String value)
		{
			if (value == null)
				value = null;
			else
				this.value = value;
		}

		/**
		 * Converts the KVPair into a CSV-style string.
		 *
		 * @return String representation.
		 */
		public String toString() {
			return this.key + "," + this.value;
		}
	}
